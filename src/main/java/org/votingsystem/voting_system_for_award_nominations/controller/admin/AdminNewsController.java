package org.votingsystem.voting_system_for_award_nominations.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.News;
import org.votingsystem.voting_system_for_award_nominations.repository.NewsRepository;

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {

    private final NewsRepository newsRepository;

    @Autowired
    public AdminNewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Displays the page for managing and adding news.
     */
    @GetMapping("")
    public String showNewsManagementPage(Model model) {
        // Fetch existing news to display in the list
        model.addAttribute("newsList", newsRepository.findAllByOrderByIdDesc());
        return "admin-news";
    }

    /**
     * Handles the submission of the "Add News" form.
     */
    @PostMapping("/add")
    public String addNews(@RequestParam String type,
                          @RequestParam String title,
                          @RequestParam String imageUrl,
                          @RequestParam String summary,
                          @RequestParam(required = false) String content,
                          @RequestParam(required = false) String externalUrl,
                          RedirectAttributes redirectAttributes) {

        News news = new News();
        news.setTitle(title);
        news.setImageUrl(imageUrl);
        news.setType(type);
        news.setSummary(summary);

        if ("STANDARD".equalsIgnoreCase(type)) {
            news.setContent(content);
        } else if ("EXTERNAL_LINK".equalsIgnoreCase(type)) {
            news.setExternalUrl(externalUrl);
        }

        newsRepository.save(news);

        redirectAttributes.addFlashAttribute("success", "News item published successfully!");
        return "redirect:/admin/news";
    }

    /**
     * Deletes a news item by its ID.
     */
    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "News item deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error: News item not found.");
        }
        return "redirect:/admin/news";
    }
}