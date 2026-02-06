package org.votingsystem.voting_system_for_award_nominations.controller.coo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.service.coo.CooService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coo")
public class CooWinnerController {

    private final CooService cooService;

    public CooWinnerController(CooService cooService) {
        this.cooService = cooService;
    }

    // Create winner
    @PostMapping("/winners")
    public String saveWinner(@RequestParam Long categoryId,
                             @RequestParam Long winnerId,
                             @RequestParam(required = false) MultipartFile winnerImage,
                             RedirectAttributes redirectAttributes) {
        try {
            cooService.saveWinner(categoryId, winnerId, winnerImage);
            redirectAttributes.addFlashAttribute("success", "✅ Winner declared successfully. Submit for review when ready.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error saving winner.");
            e.printStackTrace();
        }
        return "redirect:/coo/voting_sessions";
    }

    // === NEW METHOD START ===
    /**
     * Handles the HTTP POST request to submit all declared winners for a specific event for review by the PRO.
     * @param eventId The ID of the event, submitted from a hidden form field.
     * @param redirectAttributes Used to add flash messages for success or error feedback.
     * @return A redirect instruction to the COO's main voting sessions page.
     */
    @PostMapping("/winners/submit-for-review")
    public String submitWinnersForReview(@RequestParam Long eventId, RedirectAttributes redirectAttributes) {
        try {
            cooService.submitWinnersForReview(eventId);
            redirectAttributes.addFlashAttribute("success", "✅ Winners have been submitted to the PRO for final review.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }
        return "redirect:/coo/voting_sessions";
    }
    // === NEW METHOD END ===


    // Delete winner
    @PostMapping("/winners/{id}/delete")
    public String deleteWinner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cooService.deleteWinner(id);
            redirectAttributes.addFlashAttribute("success", "Winner deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting winner.");
        }
        return "redirect:/coo/voting_sessions";
    }

    // Update winner
    @PostMapping("/winners/{id}/update")
    public String updateWinner(@PathVariable Long id,
                               @RequestParam Long categoryId,
                               @RequestParam Long winnerId,
                               @RequestParam(required = false) MultipartFile winnerImage,
                               RedirectAttributes redirectAttributes) {
        try {
            cooService.updateWinner(id, categoryId, winnerId, winnerImage);
            redirectAttributes.addFlashAttribute("success", "Winner details updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating winner.");
            e.printStackTrace();
        }
        return "redirect:/coo/voting_sessions";
    }
}