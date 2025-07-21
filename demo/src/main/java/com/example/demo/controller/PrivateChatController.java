package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import com.example.demo.dto.MessageDTO;
import com.example.demo.model.Message;
import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPrivateInvitation;
// Hapus import RoomPrivateParticipants
// import com.example.demo.model.RoomPrivateParticipants;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomPrivateRepository;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
// Hapus import RoomPrivateParticipantsRepository
// import com.example.demo.repository.RoomPrivateParticipantsRepository;
import com.example.demo.service.UserService;
import com.example.demo.repository.RoomPrivateInvitationRepository;
import com.example.demo.service.InvitationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrivateChatController {

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomPrivateInvitationRepository invitationRepo;

    // Hapus @Autowired untuk RoomPrivateParticipantsRepository
    // @Autowired
    // private RoomPrivateParticipantsRepository roomPrivateParticipantsRepo;

    @Autowired
    private InvitationService invitationService;

    @GetMapping("/private-chat")
    public String privateChat(@RequestParam Long roomId, Model model) {
        RoomPrivate room = roomPrivateRepo.findById(roomId).orElseThrow();

        User currentUser = userService.getCurrentUser();

        // Owner bisa langsung masuk ke room mereka
        if (room.getOwner().getId().equals(currentUser.getId())) {
            model.addAttribute("room", room);
            return "private_chat_room";
        }

        // ✅ Cek apakah pengguna adalah peserta yang disetujui melalui Set<User> participants
        boolean isParticipant = room.getParticipants().contains(currentUser);
        if (!isParticipant) {
            return "redirect:/dashboard?error=access_denied";
        }

        model.addAttribute("room", room);
        return "private_chat_room";
    }

    @MessageMapping("/private.chat.send")
    public void sendPrivateMessage(@Payload Message message) {
        try {
            User user = userRepo.findById(message.getUser().getId()).orElseThrow();
            RoomPrivate room = roomPrivateRepo.findById(message.getRoomPrivate().getId()).orElseThrow();

            message.setUser(user);
            message.setRoomPrivate(room);
            message.setTimestamp(LocalDateTime.now());

            messageRepo.save(message);

            MessageDTO dto = convertToDTO(message);
            messagingTemplate.convertAndSend("/topic/private/room/" + room.getId(), dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/private/chat/history")
    @ResponseBody
    public List<MessageDTO> getPrivateChatHistory(@RequestParam Long roomId) {
        RoomPrivate room = roomPrivateRepo.findById(roomId).orElseThrow();
        List<Message> messages = messageRepo.findByRoomPrivate(room);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Tampilkan halaman input kode undangan
    @GetMapping("/join-private-room")
    public String showJoinPrivateRoom() {
        return "join_private_room";
    }

    // Proses submit kode undangan
    @PostMapping("/private/join")
    public String joinPrivateRoom(@RequestParam String code) {
        RoomPrivate room = roomPrivateRepo.findByInviteCode(code).orElseThrow();
        User currentUser = userService.getCurrentUser();

        if (room == null || room.getOwner() == null) {
            return "redirect:/join-private-room?error";
        }

        // Cek apakah user adalah pemilik atau peserta melalui Set<User> participants
        boolean isOwner = room.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = room.getParticipants().contains(currentUser);

        if (!isOwner && !isParticipant) {
            return "redirect:/join-private-room?error";
        }

        return "redirect:/private-chat?roomId=" + room.getId(); // Ubah parameter dari 'code' menjadi 'roomId'
    }

    // ✅ Hapus satu pesan
    @PostMapping("/private/message/delete")
    @Transactional
    public ResponseEntity<String> deletePrivateMessage(@RequestParam Long id) {
        Message message = messageRepo.findById(id).orElseThrow();
        User currentUser = userService.getCurrentUser();

        if (!message.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tidak diizinkan menghapus pesan ini");
        }

        messageRepo.deleteById(id);
        messagingTemplate.convertAndSend("/topic/private/message/deleted", Map.of("messageId", id));
        return ResponseEntity.ok("Pesan dihapus");
    }

// ✅ Hapus semua pesan di room privat
    @PostMapping("/private/message/delete/all")
    @Transactional
    public ResponseEntity<String> deleteAllPrivateMessages(@RequestParam Long roomId) {
        RoomPrivate room = roomPrivateRepo.findById(roomId).orElseThrow();
        User currentUser = userService.getCurrentUser();

        if (!room.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hanya owner yang bisa menghapus semua pesan");
        }

        messageRepo.deleteByRoomPrivate(room);
        messagingTemplate.convertAndSend("/topic/private/message/deleted/all", Map.of("roomId", roomId));
        return ResponseEntity.ok("Semua pesan dihapus");
    }

// ✅ Hapus pesan terpilih
    @PostMapping("/private/message/delete/selected")
    @Transactional
    public ResponseEntity<String> deleteSelectedPrivateMessages(
            @RequestParam Long roomId,
            @RequestBody Map<String, Object> payload) {

        try {
            List<Long> ids = ((List<?>) payload.get("ids")).stream()
                    .map(obj -> ((Number) obj).longValue())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            RoomPrivate room = roomPrivateRepo.findById(roomId).orElseThrow();
            User currentUser = userService.getCurrentUser();

            messageRepo.deleteByRoomPrivateIdAndUserAndIdIn(room.getId(), currentUser.getId(), ids);

            messagingTemplate.convertAndSend("/topic/private/message/deleted/selected", Map.of("ids", ids));

            return ResponseEntity.ok("Pesan terpilih dihapus");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Gagal menghapus pesan terpilih");
        }
    }

    @PostMapping("/private/invite")
    public ResponseEntity<String> sendPrivateRoomInvite(@RequestParam String inviteCode,
            @RequestParam String username) {
        RoomPrivate room = roomPrivateRepo.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Room tidak ditemukan"));
        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room tidak ditemukan");
        }

        User invitedUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        // ✅ Cek apakah user sudah menjadi peserta melalui Set<User> participants
        if (room.getParticipants().contains(invitedUser)) {
            return ResponseEntity.badRequest().body("Pengguna sudah ada di room");
        }

        RoomPrivateInvitation invitation = new RoomPrivateInvitation();
        invitation.setRoomPrivate(room);
        invitation.setInvitedUser(invitedUser);
        invitation.setStatus("PENDING");
        invitationRepo.save(invitation);

        messagingTemplate.convertAndSendToUser(
                room.getOwner().getUsername(),
                "/queue/notifications",
                Map.of("message",
                        "Undangan masuk dari " + invitedUser.getUsername() + " untuk room " + room.getName()));

        return ResponseEntity.ok("Undangan berhasil dikirim");
    }

    @PostMapping("/private/invitation/{id}/action")
    public ResponseEntity<String> respondToInvitation(
            @PathVariable Long id,
            @RequestParam String action) {

        RoomPrivateInvitation invitation = invitationRepo.findById(id).orElseThrow();
        RoomPrivate room = invitation.getRoomPrivate();

        if (!room.getOwner().getId().equals(userService.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hanya owner yang bisa memproses undangan");
        }

        if ("APPROVED".equalsIgnoreCase(action)) {
            invitation.setStatus("APPROVED");
            // ✅ Tambahkan peserta langsung ke Set<User> participants di RoomPrivate
            if (!room.getParticipants().contains(invitation.getInvitedUser())) {
                room.getParticipants().add(invitation.getInvitedUser());
                roomPrivateRepo.save(room); // Simpan room untuk persistensi ManyToMany
            }
        } else {
            invitation.setStatus("DENIED");
            // ✅ Hapus peserta dari Set<User> participants jika ditolak
            if (room.getParticipants().contains(invitation.getInvitedUser())) {
                room.getParticipants().remove(invitation.getInvitedUser());
                roomPrivateRepo.save(room); // Simpan room untuk persistensi ManyToMany
            }
        }

        invitationRepo.save(invitation); // Simpan perubahan status undangan

        return ResponseEntity.ok("Undangan diproses");
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                formatTimestamp(message.getTimestamp()),
                message.getUser().getUsername());
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        return timestamp != null
                ? timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
                : "";
    }
}