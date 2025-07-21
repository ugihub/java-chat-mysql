package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import java.util.List;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "edit_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String newPassword) {

        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        // Simpan perubahan ke database
        user.setUsername(username);
        user.setEmail(email);

        if (!newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepo.save(user);

        // ✅ Perbarui session dengan detail baru dan authorities yang sama
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        username,
                        user.getPassword(),
                        user.getAuthorities() // Gunakan authorities dari user yang sudah ada
                ),
                null,
                user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/dashboard?profileUpdated";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount() {
        User user = userService.getCurrentUser();
        userRepo.delete(user);
        return "redirect:/login?accountDeleted";
    }
}