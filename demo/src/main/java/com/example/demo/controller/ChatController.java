package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.MessageDTO;
import com.example.demo.model.Message;
import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPublic;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomPrivateRepository;
import com.example.demo.repository.RoomPublicRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import jakarta.transaction.Transactional;

@Controller
public class ChatController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoomPublicRepository roomPublicRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    private MessageDTO createErrorMessage(String message) {
        return new MessageDTO(null, message, formatTimestamp(LocalDateTime.now()), "System");
    }

    @GetMapping("/chat")
    public String chat(@RequestParam Long roomId, Model model) {
        RoomPublic room = roomPublicRepo.findById(roomId).orElseThrow();
        model.addAttribute("roomName", room.getName());
        model.addAttribute("roomId", room.getId()); // ✅ Pastikan ini benar
        return "chat_room";
    }

    /**
     * WebSocket: Kirim pesan dan broadcast ke semua klien
     */
    // ✅ Kirim pesan ke room publik
    @MessageMapping("/chat.send")
    public void sendPublicMessage(@Payload Message message) {
        try {
            // ✅ Validasi user dan room dari database
            User user = userRepo.findById(message.getUser().getId()).orElseThrow();
            RoomPublic room = roomPublicRepo.findById(message.getRoomPublic().getId()).orElseThrow();

            message.setUser(user);
            message.setRoomPublic(room);
            message.setTimestamp(LocalDateTime.now());

            messageRepo.save(message); // ✅ Simpan ke database

            // ✅ Kirim ke WebSocket dengan DTO
            MessageDTO dto = convertToDTO(message);
            messagingTemplate.convertAndSend("/topic/public/room/" + room.getId(), dto);

        } catch (Exception e) {
            e.printStackTrace();
            // ✅ Tambahkan log untuk debugging
            System.err.println("Gagal mengirim pesan: " + e.getMessage());
        }
    }

    /**
     * Endpoint: Muat riwayat chat dari database
     */
    // ✅ Endpoint untuk ambil riwayat chat
    @GetMapping("/chat/history")
    @ResponseBody
    public List<MessageDTO> getChatHistory(@RequestParam Long roomId) {
        try {
            RoomPublic room = roomPublicRepo.findById(roomId).orElseThrow();
            return messageRepo.findByRoomPublic(room).stream()
                    .map(this::convertToDTO)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ✅ Hapus pesan di room publik
    @PostMapping("/message/delete")
    @Transactional
    public ResponseEntity<String> deleteMessage(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(payload.get("id").toString());
        Message message = messageRepo.findById(id).orElseThrow();
        User currentUser = userService.getCurrentUser();

        if (!message.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tidak diizinkan menghapus pesan ini");
        }

        messageRepo.delete(message);
        messagingTemplate.convertAndSend("/topic/message/deleted", Map.of("messageId", id));
        return ResponseEntity.ok("Pesan dihapus");
    }

    @PostMapping("/message/delete/all")
    @Transactional
    public ResponseEntity<String> deleteAllMessages(@RequestBody Map<String, Long> payload) {
        Long roomId = payload.get("roomId");
        RoomPublic room = roomPublicRepo.findById(roomId).orElseThrow();
        User currentUser = userService.getCurrentUser();

        messageRepo.deleteByRoomPublicAndUser(room, currentUser);
        messagingTemplate.convertAndSend("/topic/message/deleted/all", Map.of("roomId", roomId));

        return ResponseEntity.ok("Semua pesan dihapus");
    }

    @PostMapping("/message/delete/selected")
    @Transactional
    public ResponseEntity<String> deleteSelectedMessages(@RequestBody Map<String, Object> payload) {
        try {
            // ✅ Ambil roomId dan ids dari payload
            Long roomId = ((Number) payload.get("roomId")).longValue();
            List<Long> ids = ((List<?>) payload.get("ids")).stream()
                    .map(obj -> ((Number) obj).longValue())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            // ✅ Ambil entitas dari database
            RoomPublic room = roomPublicRepo.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room tidak ditemukan"));
            User currentUser = userService.getCurrentUser();

            // ✅ Gunakan entitas langsung, bukan ID
            messageRepo.deleteByRoomPublicAndUserAndIdIn(room, currentUser, ids);

            // ✅ Broadcast ke WebSocket
            messagingTemplate.convertAndSend("/topic/message/deleted/selected", Map.of("ids", ids));

            return ResponseEntity.ok("Pesan terpilih dihapus");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Gagal menghapus pesan terpilih");
        }
    }

    // ✅ Method helper untuk konversi Message → MessageDTO
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