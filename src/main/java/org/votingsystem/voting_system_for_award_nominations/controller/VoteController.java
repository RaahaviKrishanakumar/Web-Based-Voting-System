package org.votingsystem.voting_system_for_award_nominations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.votingsystem.voting_system_for_award_nominations.modelentity.*;
import org.votingsystem.voting_system_for_award_nominations.repository.*;
import org.votingsystem.voting_system_for_award_nominations.security.CustomUserDetails;

import java.util.List;

@Controller
@RequestMapping("/vote")
public class VoteController {

    private final CategoryRepository categoryRepository;
    private final NomineeRepository nomineeRepository;
    private final VoteRepository voteRepository;
    private final EventRepository eventRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final UserRepository userRepository; // ✅ UserRepository is needed

    @Autowired
    public VoteController(CategoryRepository categoryRepository,
                          NomineeRepository nomineeRepository,
                          VoteRepository voteRepository,
                          EventRepository eventRepository,
                          VotingSessionRepository votingSessionRepository,
                          UserRepository userRepository) { // ✅ Add to constructor
        this.categoryRepository = categoryRepository;
        this.nomineeRepository = nomineeRepository;
        this.voteRepository = voteRepository;
        this.eventRepository = eventRepository;
        this.votingSessionRepository = votingSessionRepository;
        this.userRepository = userRepository; // ✅ Initialize it
    }

    //  Show all categories (only if active event & ONGOING session)
    @GetMapping("")
    public String showCategories(Model model) {
        Event activeEvent = eventRepository.findFirstByStatus("ACTIVE").orElse(null);

        if (activeEvent == null) {
            model.addAttribute("error", "❌ No active event found.");
            return "vote";
        }

        VotingSession session = votingSessionRepository
                .findTopByEvent_IdOrderByIdDesc(activeEvent.getId())
                .orElse(null);

        if (session == null || !"ONGOING".equals(session.getStatus())) {
            model.addAttribute("error", "⚠ Voting is not available right now.");
            return "vote";
        }

        List<Category> categories = categoryRepository.findByEvent(activeEvent)
                .stream()
                .filter(cat -> !nomineeRepository.findByCategoryIdAndStatus(cat.getId(), "APPROVED").isEmpty())
                .toList();

        model.addAttribute("categories", categories);
        return "vote"; // vote.html
    }

    //  Show nominees of a category
    @GetMapping("/category/{id}")
    public String showNominees(@PathVariable("id") Long categoryId, Model model) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        List<Nominee> approvedNominees = nomineeRepository.findByCategoryIdAndStatus(categoryId, "APPROVED");

        if (approvedNominees.isEmpty()) {
            model.addAttribute("error", "⚠ No approved nominees in this category.");
        }

        model.addAttribute("category", category);
        model.addAttribute("nominees", approvedNominees);
        return "vote_nominees"; // vote_nominees.html
    }

    //  Handle vote submission
    @PostMapping("/submit")
    public String submitVote(@RequestParam Long categoryId,
                             @RequestParam Long nomineeId,
                             Authentication authentication, // ✅ Get the logged-in user securely
                             RedirectAttributes redirectAttributes) {

        // ✅ Get the full User object from the security context
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        String voterId = loggedInUser.getEmail();

        // Check if voter already voted in this category
        boolean alreadyVoted = voteRepository.existsByNominee_Category_IdAndVoterId(categoryId, voterId);
        if (alreadyVoted) {
            redirectAttributes.addFlashAttribute("error", "❌ You have already voted in this category.");
            return "redirect:/vote/category/" + categoryId;
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow();
        Nominee nominee = nomineeRepository.findById(nomineeId).orElseThrow();

        if (!"APPROVED".equals(nominee.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "❌ Invalid vote! You can only vote for approved nominees.");
            return "redirect:/vote/category/" + categoryId;
        }

        // Save the vote with all details
        Vote vote = new Vote();
        vote.setNominee(nominee);
        vote.setEvent(category.getEvent());
        vote.setVoterId(voterId);

        // ✅ FIX: Copy the age and country from the logged-in user to the new vote
        vote.setAge(loggedInUser.getAge());
        vote.setCountry(loggedInUser.getCountry());

        voteRepository.save(vote);

        redirectAttributes.addFlashAttribute("success", "✅ Your vote has been recorded!");
        return "redirect:/vote";
    }
}