package org.votingsystem.voting_system_for_award_nominations.controller.hr;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;
import org.votingsystem.voting_system_for_award_nominations.service.hr.HRService;

@Controller
@RequestMapping("/hr")
public class HrPageController {

    private final HRService hrService;
    private final EventService eventService;

    public HrPageController(HRService hrService, EventService eventService) {
        this.hrService = hrService;
        this.eventService = eventService;
    }

    @GetMapping("/nominees")
    public String showNomineeManagementPage(Model model) {
        //  Only nominees for the active event
        Long activeEventId = eventService.getActiveEvent()
                .map(ev -> ev.getId())
                .orElse(null);

        model.addAttribute("pendingNominees",
                hrService.getNomineesByStatusAndEvent(activeEventId, "PENDING"));
        model.addAttribute("approvedNominees",
                hrService.getNomineesByStatusAndEvent(activeEventId, "APPROVED"));
        model.addAttribute("rejectedNominees",
                hrService.getNomineesByStatusAndEvent(activeEventId, "REJECTED"));

        model.addAttribute("categories", hrService.getCategoriesByEvent(activeEventId));


        return "HR/nomineemanage";  // resolves to templates/HR/nomineemanage.html
    }
}
