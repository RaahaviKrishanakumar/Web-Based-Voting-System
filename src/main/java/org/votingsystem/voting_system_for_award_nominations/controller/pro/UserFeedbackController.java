package org.votingsystem.voting_system_for_award_nominations.controller.pro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Feedback;
import org.votingsystem.voting_system_for_award_nominations.modelentity.User;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.pro.FeedbackService;
import org.votingsystem.voting_system_for_award_nominations.repository.UserRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/userfeedback")
public class UserFeedbackController {

    private final FeedbackService feedbackService;
    private final EventService eventService;
    private final UserRepository userRepository;

    public UserFeedbackController(FeedbackService feedbackService,
                                  EventService eventService,
                                  UserRepository userRepository) {
        this.feedbackService = feedbackService;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    // Show all feedback for active event
    @GetMapping
    public String feedbackPage(Model model, Principal principal) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            model.addAttribute("noEvent", true);
            return "userfeedback";
        }

        List<Feedback> allFeedback = feedbackService.getFeedbackForEvent(activeEvent.getId());
        model.addAttribute("event", activeEvent);
        model.addAttribute("feedbackList", allFeedback);
        model.addAttribute("noEvent", false);

        return "userfeedback";
    }

    // Add new feedback
    @PostMapping("/add")
    public String addFeedback(@RequestParam String message,
                              @RequestParam Integer rating,
                              Principal principal,
                              RedirectAttributes ra) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            ra.addFlashAttribute("error", "No active event to add feedback.");
            return "redirect:/userfeedback";
        }

        try {
            if (rating == null) {
                throw new IllegalArgumentException("Please select a star rating.");
            }
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

            feedbackService.addFeedback(activeEvent, user, message, rating);
            ra.addFlashAttribute("success", "Feedback added!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/userfeedback";
    }

    // Edit feedback
    @PostMapping("/{id}/edit")
    public String editFeedback(@PathVariable Long id,
                               @RequestParam String message,
                               @RequestParam Integer rating,
                               Principal principal,
                               RedirectAttributes ra) {
        try {
            if (rating == null) {
                throw new IllegalArgumentException("Please select a star rating.");
            }
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

            feedbackService.editFeedback(id, user, message, rating);
            ra.addFlashAttribute("success", "Feedback updated!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/userfeedback";
    }

    // Delete feedback
    @PostMapping("/{id}/delete")
    public String deleteFeedback(@PathVariable Long id,
                                 Principal principal,
                                 RedirectAttributes ra) {
        try {
            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));

            feedbackService.deleteFeedback(id, user);
            ra.addFlashAttribute("success", "Feedback deleted!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/userfeedback";
    }
}
