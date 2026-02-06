package org.votingsystem.voting_system_for_award_nominations.controller.coo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.service.coo.CooService;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;

@Controller
@RequestMapping("/coo/session")
public class CooSessionController {

    private final CooService cooService;
    private final EventService eventService;

    public CooSessionController(CooService cooService, EventService eventService) {
        this.cooService = cooService;
        this.eventService = eventService;
    }

    @PostMapping("/start")
    public String startSession(RedirectAttributes redirectAttributes) {
        return eventService.getActiveEvent()
                .map(event -> {
                    try {
                        cooService.startSession(event.getId());
                        redirectAttributes.addFlashAttribute("success", "✅ Voting session started successfully!");
                    } catch (RuntimeException ex) {
                        redirectAttributes.addFlashAttribute("error", ex.getMessage());
                    }
                    return "redirect:/coo/voting_sessions";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "❌ No active event available to start a session.");
                    return "redirect:/coo/voting_sessions";
                });
    }

    @PostMapping("/restart")
    public String restartSession(@RequestParam(value = "clearVotes", defaultValue = "false") boolean clearVotes,
                                 RedirectAttributes redirectAttributes) {
        //  Require ticking "Clear All Votes" checkbox
        if (!clearVotes) {
            redirectAttributes.addFlashAttribute("error", "⚠️ You must tick 'Clear All Votes' to restart the session.");
            return "redirect:/coo/voting_sessions";
        }

        return eventService.getActiveEvent()
                .map(event -> {
                    try {
                        cooService.restartSession(event.getId(), true); // always true since required
                        redirectAttributes.addFlashAttribute("success", "🔄 Session restarted and all votes cleared!");
                    } catch (RuntimeException ex) {
                        redirectAttributes.addFlashAttribute("error", ex.getMessage());
                    }
                    return "redirect:/coo/voting_sessions";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "❌ No active event to restart.");
                    return "redirect:/coo/voting_sessions";
                });
    }

    @PostMapping("/end")
    public String endSession(RedirectAttributes redirectAttributes) {
        return eventService.getActiveEvent()
                .map(event -> {
                    try {
                        cooService.endSession(event.getId());
                        redirectAttributes.addFlashAttribute("success", "🛑 Voting session ended successfully!");
                    } catch (RuntimeException ex) {
                        redirectAttributes.addFlashAttribute("error", ex.getMessage());
                    }
                    return "redirect:/coo/voting_sessions";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "❌ No active event to end.");
                    return "redirect:/coo/voting_sessions";
                });
    }

    @GetMapping("/status")
    @ResponseBody
    public String sessionStatus() {
        // later can wire to cooService.getSessionStatus(eventId)
        return "ONGOING/ENDED/NOT_STARTED";
    }
}
