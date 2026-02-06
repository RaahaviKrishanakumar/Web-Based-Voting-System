package org.votingsystem.voting_system_for_award_nominations.controller.em;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.service.em.NomineeService;

import java.io.IOException;

@Controller
@RequestMapping("/admin/nominees")
public class NomineeController {

    private final NomineeService nomineeService;

    public NomineeController(NomineeService nomineeService) {
        this.nomineeService = nomineeService;
    }

    // Add nominee
    @PostMapping("/create")
    public String createNominee(@ModelAttribute Nominee nominee,
                                @RequestParam("nomineeImage") MultipartFile file) throws IOException {
        nomineeService.createNominee(nominee, file);
        return "redirect:/admin/eventmanage";
    }

    // Delete nominee
    @PostMapping("/delete/{id}")
    public String deleteNominee(@PathVariable Long id) {
        nomineeService.deleteNominee(id);
        return "redirect:/admin/eventmanage";
    }
}
