package org.votingsystem.voting_system_for_award_nominations.controller.em;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.service.em.CategoryService;
import org.votingsystem.voting_system_for_award_nominations.service.em.EventService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final EventService eventService;

    public CategoryController(CategoryService categoryService, EventService eventService) {
        this.categoryService = categoryService;
        this.eventService = eventService;
    }

    // Add category → always linked to the active event
    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category) {
        eventService.getActiveEvent().ifPresent(category::setEvent);
        categoryService.createCategory(category);
        return "redirect:/admin/eventmanage";
    }

    // Delete category
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/eventmanage";
    }
}
