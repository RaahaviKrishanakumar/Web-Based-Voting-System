package org.votingsystem.voting_system_for_award_nominations.controller.pro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.pro.FeedbackService;
import org.votingsystem.voting_system_for_award_nominations.service.pro.ProService; // You must create this new service

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pro") // The base URL for all PRO actions
public class ProController {

    private final FeedbackService feedbackService;
    private final EventService eventService;
    private final ProService proService; // The new service for winner logic

    @Autowired
    public ProController(FeedbackService feedbackService, EventService eventService, ProService proService) {
        this.feedbackService = feedbackService;
        this.eventService = eventService;
        this.proService = proService;
    }

    // --- Feedback Management Methods ---

    /**
     * Shows the feedback management page for the active event.
     * Accessible at GET /pro/feedback
     */
    @GetMapping("/feedback")
    public String feedbackPage(Model model) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            model.addAttribute("noEvent", true);
            return "pro/feedback";
        }

        model.addAttribute("noEvent", false);
        model.addAttribute("event", activeEvent);
        model.addAttribute("feedbackList", feedbackService.getFeedbackForEvent(activeEvent.getId()));
        return "pro/feedback";
    }

    /**
     * Handles the submission of a reply to a specific feedback item.
     * Accessible at POST /pro/feedback/{id}/reply
     */
    @PostMapping("/feedback/{id}/reply")
    public String replyFeedback(@PathVariable Long id,
                                @RequestParam String replyMessage,
                                RedirectAttributes ra) {
        try {
            feedbackService.replyToFeedback(id, replyMessage);
            ra.addFlashAttribute("success", "Reply added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error adding reply: " + e.getMessage());
        }
        return "redirect:/pro/feedback";
    }

    //Handles the request to delete a feedback item
    @PostMapping("/feedback/{id}/delete")
    public String deleteFeedback(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedbackByAdmin(id);
            redirectAttributes.addFlashAttribute("success", "Feedback has been deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting feedback: " + e.getMessage());
        }
        return "redirect:/pro/feedback";
    }


    // --- Winner Publishing Methods ---

    /**
     * Displays the page for the PRO to review and publish winners.
     * Accessible at GET /pro/winners/review
     */
    @GetMapping("/winners/review")
    public String showWinnerReviewPage(Model model) {
        model.addAttribute("pendingWinners", proService.getWinnersPendingReview());
        return "pro/winner-review"; // Points to your new winner review HTML file
    }

    /**
     * Handles the form submission to publish winners.
     * Accessible at POST /pro/winners/publish
     */
    @PostMapping("/winners/publish")
    public String publishWinners(@RequestParam Map<String, String> allRequestParams, RedirectAttributes redirectAttributes) {
        // This logic filters the form data to get only the descriptions for each winner
        Map<Long, String> winnerDescriptions = allRequestParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("description-"))
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey().substring("description-".length())),
                        Map.Entry::getValue
                ));

        try {
            proService.publishWinners(winnerDescriptions);
            redirectAttributes.addFlashAttribute("success", "🎉 Winners have been published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error publishing winners: " + e.getMessage());
        }

        return "redirect:/pro/winners/review";
    }
}