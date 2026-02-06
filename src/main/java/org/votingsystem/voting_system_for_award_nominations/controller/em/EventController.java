package org.votingsystem.voting_system_for_award_nominations.controller.em;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;

@Controller
@RequestMapping("/admin/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Create new event
    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event) {
        eventService.createEvent(event);
        return "redirect:/admin/eventmanage";
    }

    // Close event
    @PostMapping("/close/{id}")
    public String closeEvent(@PathVariable Long id) {
        eventService.closeEvent(id);
        return "redirect:/admin/eventmanage";
    }
    // Update event (only for ACTIVE)
    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event updatedEvent) {
        eventService.updateEvent(id, updatedEvent);
        return "redirect:/admin/eventmanage";
    }

}
