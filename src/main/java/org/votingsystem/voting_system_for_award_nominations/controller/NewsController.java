package org.votingsystem.voting_system_for_award_nominations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.votingsystem.voting_system_for_award_nominations.factory.NewsItem;
import org.votingsystem.voting_system_for_award_nominations.factory.NewsItemFactory;
import org.votingsystem.voting_system_for_award_nominations.modelentity.News;
import org.votingsystem.voting_system_for_award_nominations.repository.NewsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NewsController {

    private final NewsRepository newsRepository;
    private final NewsItemFactory newsItemFactory;

    @Autowired
    public NewsController(NewsRepository newsRepository, NewsItemFactory newsItemFactory) {
        this.newsRepository = newsRepository;
        this.newsItemFactory = newsItemFactory;
    }

    /**
     * Prepares the news list for the user dashboard.
     * This method uses the Factory to rebuild the correct NewsItem objects.
     */
    @GetMapping("/userdashboard")
    public String showDashboard(Model model) {
        // 1. Fetch the raw data from the database
        List<News> newsEntities = newsRepository.findAllByOrderByIdDesc();

        // 2. Use the factory to create a list of displayable NewsItem objects
        List<NewsItem> newsItems = newsEntities.stream()
                .map(news -> newsItemFactory.createNewsItem(
                        news.getType(),
                        news.getId(),
                        news.getTitle(),
                        news.getImageUrl(),
                        news.getExternalUrl(),
                        news.getSummary()
                ))
                .collect(Collectors.toList());

        model.addAttribute("newsItems", newsItems);
        return "userdashboard";
    }

    /**
     * Displays the full details page for a single "Standard Article".
     */
    @GetMapping("/news/{id}")
    public String showNewsDetails(@PathVariable Long id, Model model) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));

        // This page is only for standard articles, so we redirect if it's an external link
        if (!"STANDARD".equalsIgnoreCase(news.getType())) {
            return "redirect:/userdashboard";
        }

        model.addAttribute("news", news);
        return "news-details";
    }
}