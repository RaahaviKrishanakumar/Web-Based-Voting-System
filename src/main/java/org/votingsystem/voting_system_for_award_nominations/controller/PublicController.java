package org.votingsystem.voting_system_for_award_nominations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Winner;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.user.PublicService;

import java.util.List;
import java.util.Optional;

@Controller
public class PublicController {

    private final PublicService publicService;
    private final EventService eventService;

    @Autowired
    public PublicController(PublicService publicService, EventService eventService) {
        this.publicService = publicService;
        this.eventService = eventService;
    }

    /**
     * Displays the public winners page.
     * It checks if there's an active event and if winners have been published.
     * @param model The Spring Model to pass data to the view.
     * @return The name of the winners HTML template.
     */
    @GetMapping("/winners")
    public String showPublicWinnersPage(Model model) {
        // First, check if there is any event currently marked as "ACTIVE"
        Optional<Event> activeEventOpt = eventService.getActiveEvent();

        if (activeEventOpt.isEmpty()) {
            // SCENARIO 3: No active event exists.
            model.addAttribute("noActiveEvent", true);
        } else {
            // An active event exists, so now we check for winners.
            model.addAttribute("noActiveEvent", false);
            model.addAttribute("event", activeEventOpt.get());

            List<Winner> winners = publicService.getPublishedWinnersForActiveEvent();
            model.addAttribute("publishedWinners", winners);
            // The view will handle the logic for SCENARIO 1 vs SCENARIO 2
            // by checking if the 'publishedWinners' list is empty.
        }

        return "winners"; // This is the path to your new HTML file
    }
}