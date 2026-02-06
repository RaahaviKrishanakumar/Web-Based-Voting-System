package org.votingsystem.voting_system_for_award_nominations.controller.fm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.service.fm.FinanceService;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/fm/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final EventService eventService;

    public FinanceController(FinanceService financeService, EventService eventService) {
        this.financeService = financeService;
        this.eventService = eventService;
    }

    // ---------- Dashboard ----------
    @GetMapping
    public String financePage(Model model,
                              @RequestParam(value = "msg", required = false) String msg,
                              @RequestParam(value = "err", required = false) String err) {

        Event activeEvent = eventService.getActiveEvent().orElse(null);

        if (activeEvent == null) {
            model.addAttribute("noEvent", true);
            model.addAttribute("event", null);
            model.addAttribute("sponsors", List.of());
            model.addAttribute("budgets", List.of());
            model.addAttribute("expenses", List.of());
        } else {
            model.addAttribute("noEvent", false);
            model.addAttribute("event", activeEvent);
            model.addAttribute("sponsors", financeService.listSponsors(activeEvent.getId()));
            model.addAttribute("budgets", financeService.listBudgets(activeEvent.getId()));
            model.addAttribute("expenses", financeService.listExpenses(activeEvent.getId()));
        }

        if (msg != null) model.addAttribute("success", msg);
        if (err != null) model.addAttribute("error", err);

        return "fm/finance";
    }

    // ---------- Sponsors ----------
    @PostMapping("/sponsor")
    public String addSponsor(@RequestParam String name,
                             @RequestParam BigDecimal amount,
                             @RequestParam(required = false, name = "agreement") MultipartFile agreement,
                             RedirectAttributes ra) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            ra.addAttribute("err", "No active event!");
            return "redirect:/fm/finance";
        }

        try {
            financeService.addSponsor(activeEvent, name, amount, agreement);
            ra.addAttribute("msg", "Sponsor added");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    @PostMapping("/sponsor/{id}/edit")
    public String updateSponsor(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam BigDecimal amount,
                                RedirectAttributes ra) {
        try {
            financeService.updateSponsor(id, name, amount);
            ra.addAttribute("msg", "Sponsor updated");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    @PostMapping("/sponsor/{id}/delete")
    public String deleteSponsor(@PathVariable Long id, RedirectAttributes ra) {
        try {
            financeService.deleteSponsor(id);
            ra.addAttribute("msg", "Sponsor deleted");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    // ---------- Budgets ----------
    @PostMapping("/budget")
    public String addBudget(@RequestParam String category,
                            @RequestParam BigDecimal allocatedAmount,
                            RedirectAttributes ra) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            ra.addAttribute("err", "No active event!");
            return "redirect:/fm/finance";
        }

        try {
            financeService.addBudget(activeEvent, category, allocatedAmount);
            ra.addAttribute("msg", "Budget added");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    @PostMapping("/budget/{id}/delete")
    public String deleteBudget(@PathVariable Long id, RedirectAttributes ra) {
        try {
            financeService.deleteBudget(id);
            ra.addAttribute("msg", "Budget deleted");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    // ---------- Expenses ----------
    @PostMapping("/expense")
    public String addExpense(@RequestParam Long budgetId,
                             @RequestParam String description,
                             @RequestParam BigDecimal amount,
                             RedirectAttributes ra) {
        Event activeEvent = eventService.getActiveEvent().orElse(null);
        if (activeEvent == null) {
            ra.addAttribute("err", "No active event!");
            return "redirect:/fm/finance";
        }

        try {
            financeService.addExpense(budgetId, description, amount);
            ra.addAttribute("msg", "Expense added");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }

    @PostMapping("/expense/{id}/delete")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes ra) {
        try {
            financeService.deleteExpense(id);
            ra.addAttribute("msg", "Expense deleted");
        } catch (Exception e) {
            ra.addAttribute("err", e.getMessage());
        }
        return "redirect:/fm/finance";
    }
}
