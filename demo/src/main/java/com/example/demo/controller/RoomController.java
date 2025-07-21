package com.example.demo.controller;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.transaction.Transactional;

import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPublic;
import com.example.demo.model.User;
import com.example.demo.repository.RoomPrivateRepository;
import com.example.demo.repository.RoomPublicRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.UserService;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomPrivateInvitationRepository;

@Controller
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @Autowired
    private RoomPublicRepository roomPublicRepo;

    @Autowired
    private UserRepository userRepo; // ✅ Pastikan ini ada dan di-autowire

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private RoomPrivateInvitationRepository invitationRepo;

    @PostMapping("/public")
    public String createPublicRoom(@RequestParam String name) {
        RoomPublic room = new RoomPublic();
        room.setName(name);

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login?error=notLoggedIn";
        }
        // ✅ Ambil ulang user untuk memastikan dia managed entity dalam transaksi ini
        User managedUser = userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Current user not found in DB."));

        room.setOwner(managedUser); // Set owner sebagai managed user
        roomPublicRepo.save(room);
        return "redirect:/dashboard?publicRoomCreated";
    }

    @PostMapping("/private")
    public String createPrivateRoom(@RequestParam String name) {
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        RoomPrivate room = new RoomPrivate();
        room.setName(name);
        room.setInviteCode(inviteCode);

        // ✅ Set owner sebagai user login
        room.setOwner(userService.getCurrentUser());

        roomPrivateRepo.save(room);
        return "redirect:/dashboard?privateRoomCreated&code=" + inviteCode;
    }

    @GetMapping("/list")
    public String listRooms(Model model) {
        model.addAttribute("publicRooms", chatService.getAllPublicRooms());
        model.addAttribute("privateRooms", chatService.getAllPrivateRooms());
        return "dashboard";
    }

    @PostMapping("/invite")
    public String inviteUserToPrivateRoom(
            @RequestParam String roomIdStr,
            @RequestParam String username,
            Model model) {

        try {
            Long roomId = Long.parseLong(roomIdStr);
            RoomPrivate room = roomPrivateRepo.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room tidak ditemukan"));

            User userToInvite = userRepo.findByUsername(username) // ✅ Gunakan nama variabel yang jelas
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // ✅ Ambil ulang room dari repo untuk memastikan ia managed, jika ada keraguan
            RoomPrivate managedRoom = roomPrivateRepo.findById(room.getId())
                    .orElseThrow(() -> new RuntimeException("Room tidak ditemukan di DB."));

            // ✅ Ambil ulang user yang diundang untuk memastikan ia managed
            User managedUserToInvite = userRepo.findById(userToInvite.getId())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan di DB."));

            if (!managedRoom.getParticipants().contains(managedUserToInvite)) {
                managedRoom.getParticipants().add(managedUserToInvite);
                roomPrivateRepo.save(managedRoom);
            }

            return "redirect:/dashboard";
        } catch (NumberFormatException e) {
            model.addAttribute("error", "ID Room harus berupa angka");
            return "dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "dashboard";
        }
    }

    @PostMapping("/public/delete")
    @Transactional
    public String deletePublicRoom(@RequestParam Long roomId) {
        RoomPublic room = roomPublicRepo.findById(roomId).orElseThrow();

        if (!room.getOwner().getId().equals(userService.getCurrentUser().getId())) {
            return "redirect:/dashboard?unauthorized";
        }

        messageRepo.deleteByRoomPublic(room);
        roomPublicRepo.delete(room);
        return "redirect:/dashboard";
    }

    @PostMapping("/private/delete")
    @Transactional
    public String deletePrivateRoom(@RequestParam Long roomId) {
        RoomPrivate room = roomPrivateRepo.findById(roomId).orElseThrow();
        User currentUser = userService.getCurrentUser();

        if (!room.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/dashboard?unauthorized";
        }

        // Hapus semua relasi ManyToMany (participants) secara manual
        room.getParticipants().clear();
        roomPrivateRepo.save(room); // Simpan perubahan untuk menghapus entri di tabel penghubung

        // Hapus semua undangan terkait secara manual
        invitationRepo.deleteByRoomPrivate(room);

        // Hapus semua pesan di room ini
        messageRepo.deleteByRoomPrivate(room);

        // Sekarang RoomPrivate dapat dihapus dengan aman
        roomPrivateRepo.deleteById(roomId);

        return "redirect:/dashboard";
    }
}
