package org.votingsystem.voting_system_for_award_nominations.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.votingsystem.voting_system_for_award_nominations.repository.*;
import org.votingsystem.voting_system_for_award_nominations.modelentity.*;

import java.util.*;

@Controller
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VoteRepository voteRepository;
    private final EventRepository eventRepository;
    private final NomineeRepository nomineeRepository;

    public AdminDashboardController(UserRepository userRepository,
                                    CategoryRepository categoryRepository,
                                    VoteRepository voteRepository,
                                    EventRepository eventRepository,
                                    NomineeRepository nomineeRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.voteRepository = voteRepository;
        this.eventRepository = eventRepository;
        this.nomineeRepository = nomineeRepository;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // Basic stats
        long totalUsers = userRepository.count();
        long totalCategories = categoryRepository.count();
        long totalVotes = voteRepository.count();

        Event activeEvent = eventRepository.findFirstByStatus("ACTIVE").orElse(null);
        String activeEventName = (activeEvent != null) ? activeEvent.getName() : "No Active Event";

        // Votes per Category
        Map<String, Long> votesPerCategory = new LinkedHashMap<>();
        Map<String, String> leadingNominee = new LinkedHashMap<>();

        if (activeEvent != null) {
            categoryRepository.findByEvent(activeEvent).forEach(cat -> {
                // count total votes in this category
                List<Object[]> results = voteRepository.countVotesByNominee(cat.getId());
                long totalCatVotes = results.stream()
                        .mapToLong(r -> (Long) r[1])
                        .sum();
                votesPerCategory.put(cat.getName(), totalCatVotes);

                // find leader nominee
                String leader = "None";
                long maxVotes = 0;

                for (Object[] row : results) {
                    Long nomineeId = (Long) row[0];
                    Long count = (Long) row[1];

                    String nomineeName = nomineeRepository.findById(nomineeId)
                            .map(Nominee::getName)
                            .orElse("Unknown");

                    if (count > maxVotes) {
                        maxVotes = count;
                        leader = nomineeName + " (" + count + ")";
                    }
                }

                leadingNominee.put(cat.getName(), leader);
            });
        }

        // Add to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("activeEvent", activeEventName);
        model.addAttribute("votesPerCategory", votesPerCategory);
        model.addAttribute("leadingNominee", leadingNominee);

        return "admindashboard";

    }
}
