package com.example.demo.controller;

import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.RoomPrivate;
import com.example.demo.model.RoomPrivateInvitation;
import com.example.demo.model.RoomPublic;
import com.example.demo.model.User;
// Hapus import RoomPrivateParticipants jika tidak lagi digunakan
// import com.example.demo.model.RoomPrivateParticipants;
import com.example.demo.repository.RoomPrivateRepository;
import com.example.demo.repository.RoomPublicRepository;
import com.example.demo.service.ChatService;
import com.example.demo.service.UserService;
import com.example.demo.repository.RoomPrivateInvitationRepository;
// Hapus import RoomPrivateParticipantsRepository jika tidak lagi digunakan
// import com.example.demo.repository.RoomPrivateParticipantsRepository;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RoomPublicRepository roomPublicRepo;

    @Autowired
    private RoomPrivateRepository roomPrivateRepo;

    @Autowired
    private RoomPrivateInvitationRepository invitationRepo;

    // Hapus @Autowired untuk RoomPrivateParticipantsRepository
    // @Autowired
    // private RoomPrivateParticipantsRepository roomPrivateParticipantsRepo;
    @GetMapping("/")
    public String welcome() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return "redirect:/register?usernameExists";
        }
        if (userService.existsByEmail(user.getEmail())) {
            return "redirect:/register?emailExists";
        }
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<RoomPrivate> ownedRooms = roomPrivateRepo.findByOwnerId(currentUser.getId());

        // Mengambil room yang diikuti sebagai peserta menggunakan metode Many-to-Many
        // Ini akan secara otomatis melihat tabel room_private_participants
        List<RoomPrivate> participantRooms = roomPrivateRepo.findByParticipantsContaining(currentUser);

        List<RoomPrivateInvitation> incomingInvitations = invitationRepo.findByRoomPrivateOwnerId(currentUser.getId());

        List<RoomPublic> publicRooms = roomPublicRepo.findAll();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("publicRooms", publicRooms);
        model.addAttribute("ownedRooms", ownedRooms);
        model.addAttribute("participantRooms", participantRooms);
        model.addAttribute("incomingInvitations", incomingInvitations);

        return "dashboard";
    }
}

