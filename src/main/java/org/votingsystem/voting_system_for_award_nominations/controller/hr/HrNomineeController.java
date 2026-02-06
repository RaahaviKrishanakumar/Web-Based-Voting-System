package org.votingsystem.voting_system_for_award_nominations.controller.hr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.service.hr.HRService;

@Controller
@RequestMapping("/hr/nominee")
public class HrNomineeController {

    private final HRService hrService;

    public HrNomineeController(HRService hrService) {
        this.hrService = hrService;
    }

    @PostMapping("/approve/{id}")
    public String approveNominee(@PathVariable Long id) {
        hrService.updateNomineeStatus(id, "APPROVED");
        return "redirect:/hr/nominees";
    }

    @PostMapping("/reject/{id}")
    public String rejectNominee(@PathVariable Long id) {
        hrService.updateNomineeStatus(id, "REJECTED");
        return "redirect:/hr/nominees";
    }

    @PostMapping("/delete/{id}")
    public String deleteNominee(@PathVariable Long id) {
        hrService.deleteNominee(id);
        return "redirect:/hr/nominees";
    }

    // 🔹 Update nominee details (with category + optional image)
    @PostMapping("/update/{id}")
    public String updateNominee(@PathVariable Long id,
                                @ModelAttribute Nominee nominee,
                                @RequestParam("categoryId") Long categoryId,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        nominee.setId(id); // ensure correct nominee is updated
        hrService.updateNomineeDetails(nominee, categoryId, imageFile);
        return "redirect:/hr/nominees";
    }
}
