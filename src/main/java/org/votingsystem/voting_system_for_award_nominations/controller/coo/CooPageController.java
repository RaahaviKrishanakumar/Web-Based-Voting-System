package org.votingsystem.voting_system_for_award_nominations.controller.coo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.repository.WinnerRepository;
import org.votingsystem.voting_system_for_award_nominations.service.coo.CooService;
import org.votingsystem.voting_system_for_award_nominations.repository.CategoryRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.NomineeRepository;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/coo")
public class CooPageController {

    private final CooService cooService;
    private final EventService eventService;
    private final CategoryRepository categoryRepository;
    private final NomineeRepository nomineeRepository;
    private final WinnerRepository winnerRepository;

    public CooPageController(CooService cooService,
                             EventService eventService,
                             CategoryRepository categoryRepository,
                             NomineeRepository nomineeRepository,
                             WinnerRepository winnerRepository) {
        this.cooService = cooService;
        this.eventService = eventService;
        this.categoryRepository = categoryRepository;
        this.nomineeRepository = nomineeRepository;
        this.winnerRepository = winnerRepository;
    }

    //  Load COO Voting Sessions Page
    @GetMapping("/voting_sessions")
    public String showVotingSessionsPage(Model model) {
        Optional<Event> activeEventOpt = eventService.getActiveEvent();

        if (activeEventOpt.isPresent()) {
            // Case 1: An active event is found
            Event activeEvent = activeEventOpt.get();
            Long eventId = activeEvent.getId();

            model.addAttribute("event", activeEvent);
            model.addAttribute("noActiveEvent", false);

            List<Category> categories = categoryRepository.findByEvent(activeEvent);
            model.addAttribute("categories", categories);
            model.addAttribute("approvedNominees", nomineeRepository.findByCategoryEventIdAndStatus(eventId, "APPROVED"));
            model.addAttribute("sessionStatus", cooService.getSessionStatus(eventId));

            // Add all types of winners for the view
            model.addAttribute("winners", cooService.getWinnersForEvent(eventId));
            model.addAttribute("declaredWinners", winnerRepository.findByEvent_IdAndStatus(eventId, "DECLARED"));
            model.addAttribute("pendingWinners", winnerRepository.findByEvent_IdAndStatus(eventId, "PENDING_REVIEW"));

            if (!categories.isEmpty()) {
                model.addAttribute("defaultCategoryId", categories.get(0).getId());
            }

        } else {
            // Case 2: No active event is found. Provide defaults to prevent template errors.
            model.addAttribute("noActiveEvent", true);
            model.addAttribute("event", null);
            model.addAttribute("categories", List.of());
            model.addAttribute("approvedNominees", List.of());
            model.addAttribute("sessionStatus", "NO_ACTIVE_EVENT");
            model.addAttribute("winners", List.of());
            model.addAttribute("declaredWinners", List.of());
            model.addAttribute("pendingWinners", List.of());
        }

        return "coo/voting_sessions";
    }

    //  Live Results API (AJAX)
    @GetMapping("/live-results")
    @ResponseBody
    public List<Map<String, Object>> getLiveResults(@RequestParam Long categoryId) {
        return cooService.getLiveResultsWithNames(categoryId);
    }
}