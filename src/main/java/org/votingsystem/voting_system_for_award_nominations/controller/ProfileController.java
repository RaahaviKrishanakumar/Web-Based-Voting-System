package org.votingsystem.voting_system_for_award_nominations.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.User;
import org.votingsystem.voting_system_for_award_nominations.repository.UserRepository;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Show profile
    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // Update profile info (without password)
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                Authentication authentication,
                                Model model) {
        String email = authentication.getName();
        User existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser == null) {
            return "redirect:/login";
        }

        // Keep ID, email, role, password unchanged
        updatedUser.setId(existingUser.getId());
        updatedUser.setEmail(existingUser.getEmail());
        updatedUser.setRole(existingUser.getRole());
        updatedUser.setPassword(existingUser.getPassword());

        User saved = userRepository.save(updatedUser);

        model.addAttribute("user", saved);
        model.addAttribute("successMessage", "✅ Profile updated successfully!");
        return "profile";
    }

    // Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "❌ Passwords do not match!");
            return "profile";
        }

        if (newPassword.length() < 6) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "❌ Password must be at least 6 characters long.");
            return "profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        model.addAttribute("user", user);
        model.addAttribute("successMessage", "✅ Password updated successfully!");
        return "profile";
    }

    // Delete account
    @PostMapping("/delete")
    public String deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            userRepository.deleteById(user.getId());
        }
        return "redirect:/";
    }
}
