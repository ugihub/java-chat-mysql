package com.example.demo.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPrivateInvitation;
import com.example.demo.model.User;
import com.example.demo.repository.RoomPrivateInvitationRepository;
import com.example.demo.repository.RoomPrivateRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.UserService;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/invite")
public class InviteController {

    private static final Logger logger = LoggerFactory.getLogger(InviteController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private RoomPrivateRepository roomRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoomPrivateInvitationRepository invitationRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @PostMapping("/join")
    @Transactional
    public String joinPrivateRoom(@RequestParam String code, @RequestParam Long userId) {
        RoomPrivate room = roomRepo.findByInviteCode(code)
                .orElseThrow(() -> new RuntimeException("Room tidak ditemukan."));

        User invitedUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan."));

        // Cek apakah user sudah menjadi peserta atau owner
        if (room.getParticipants().contains(invitedUser) || room.getOwner().getId().equals(invitedUser.getId())) {
            // Jika sudah ada di participants atau owner, langsung masuk
            return "redirect:/private-chat?roomId=" + room.getId();
        }

        Optional<RoomPrivateInvitation> existing = invitationRepo.findByRoomPrivateAndInvitedUser(room, invitedUser);
        if (existing.isPresent()) {
            if ("APPROVED".equals(existing.get().getStatus())) {
                // Jika sudah disetujui, tambahkan langsung ke peserta jika belum ada (untuk antisipasi inkonsistensi)
                if (!room.getParticipants().contains(invitedUser)) {
                    room.getParticipants().add(invitedUser);
                    roomRepo.save(room); // Simpan perubahan pada room
                }
                return "redirect:/private-chat?roomId=" + room.getId();
            } else if ("PENDING".equals(existing.get().getStatus())) {
                // Beri tahu jika permintaan sedang menunggu
                return "redirect:/join-private-room?pending";
            } else if ("DENIED".equals(existing.get().getStatus())) {
                // Beri tahu jika permintaan sudah ditolak
                return "redirect:/join-private-room?denied";
            }
        }

        // Jika belum ada undangan, buat yang baru
        RoomPrivateInvitation invitation = new RoomPrivateInvitation();
        invitation.setRoomPrivate(room);
        invitation.setInvitedUser(invitedUser);
        invitation.setStatus("PENDING");
        invitationRepo.save(invitation);

        messagingTemplate.convertAndSendToUser(
                room.getOwner().getUsername(),
                "/queue/notifications",
                Map.of(
                        "message", "Permintaan akses dari " + invitedUser.getUsername() + " ke room " + room.getName(),
                        "invitationId", invitation.getId(),
                        "roomId", room.getId()
                )
        );

        return "redirect:/join-private-room?pending";
    }

    @PostMapping("/respond")
    @Transactional
    public ResponseEntity<Void> respondToInvitation(@RequestBody Map<String, Object> payload) {
        try {
            Long invitationId = Long.valueOf(payload.get("invitationId").toString());
            String response = payload.get("response").toString();

            RoomPrivateInvitation invitation = invitationRepo.findById(invitationId).orElseThrow();
            RoomPrivate room = invitation.getRoomPrivate();
            User invitedUser = invitation.getInvitedUser();

            if (!room.getOwner().getId().equals(userService.getCurrentUser().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            invitation.setStatus(response.toUpperCase());
            invitationRepo.save(invitation);

            if ("APPROVED".equals(response.toUpperCase())) {
                if (!room.getParticipants().contains(invitedUser)) {
                    room.getParticipants().add(invitedUser);
                    roomPrivateRepo.save(room);
                }

                // ✅ Kirim notifikasi dengan tipe APPROVED
                messagingTemplate.convertAndSendToUser(
                        invitedUser.getUsername(),
                        "/queue/notifications",
                        Map.of(
                                "message", "Undangan ke room " + room.getName() + " disetujui",
                                "type", "APPROVED"
                        )
                );

            } else if ("DENIED".equals(response.toUpperCase())) {
                if (room.getParticipants().contains(invitedUser)) {
                    room.getParticipants().remove(invitedUser);
                    roomPrivateRepo.save(room);
                }

                // ✅ Kirim notifikasi dengan tipe DENIED
                messagingTemplate.convertAndSendToUser(
                        invitedUser.getUsername(),
                        "/queue/notifications",
                        Map.of(
                                "message", "Undangan ke room " + room.getName() + " ditolak",
                                "type", "DENIED"
                        )
                );
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
