package org.votingsystem.voting_system_for_award_nominations.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.User;
import org.votingsystem.voting_system_for_award_nominations.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Show login page (with success/error messages)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "success", required = false) String success,
                                @RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (success != null) {
            model.addAttribute("successMessage", "Registration successful! Please log in.");
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password!");
        }
        model.addAttribute("user", new User()); // for register modal
        return "index"; // loads templates/index.html
    }

    // Registration (POST)
    @PostMapping("/register")
    public String processRegister(User user, Model model) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email already registered!");
            return "index";
        }
        //  Password length validation
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            model.addAttribute("errorMessage", "Password must be at least 6 characters long!");
            return "index";
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role
        user.setRole("USER");

        userRepository.save(user);
        return "redirect:/login?success"; // back with success message
    }
}
