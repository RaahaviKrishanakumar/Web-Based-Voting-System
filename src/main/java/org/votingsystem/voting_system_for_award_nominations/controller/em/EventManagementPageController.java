package org.votingsystem.voting_system_for_award_nominations.controller.em;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.em.CategoryService;
import org.votingsystem.voting_system_for_award_nominations.service.em.NomineeService;

import java.util.List;

@Controller
public class EventManagementPageController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final NomineeService nomineeService;

    public EventManagementPageController(EventService eventService,
                                         CategoryService categoryService,
                                         NomineeService nomineeService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.nomineeService = nomineeService;
    }

    @GetMapping("/admin/eventmanage")
    public String showEventManagementPage(Model model) {
        // always show all events (history)
        model.addAttribute("events", eventService.getAllEvents());

        eventService.getActiveEvent().ifPresentOrElse(activeEvent -> {
            model.addAttribute("categories", categoryService.getCategoriesByEvent(activeEvent));
            model.addAttribute("nominees", nomineeService.getNomineesForEvent(activeEvent.getId()));
        }, () -> {
            model.addAttribute("categories", List.of());
            model.addAttribute("nominees", List.of());
        });

        // empty objects for form binding
        model.addAttribute("event", new Event());
        model.addAttribute("category", new Category());
        model.addAttribute("nominee", new Nominee());

        return "EM/eventmanage";
    }
}
