package org.votingsystem.voting_system_for_award_nominations.service.pro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Winner;
import org.votingsystem.voting_system_for_award_nominations.repository.WinnerRepository;

import java.util.List;
import java.util.Map;

@Service
public class ProService {

    private final WinnerRepository winnerRepository;

    @Autowired
    public ProService(WinnerRepository winnerRepository) {
        this.winnerRepository = winnerRepository;
    }

    /** Fetches all winners awaiting publication. */
    public List<Winner> getWinnersPendingReview() {
        return winnerRepository.findByStatus("PENDING_REVIEW");
    }

    /** Publishes winners by updating their status and adding a description. */
    @Transactional
    public void publishWinners(Map<Long, String> winnerDescriptions) {
        if (winnerDescriptions == null || winnerDescriptions.isEmpty()) {
            throw new IllegalArgumentException("No winner descriptions were provided.");
        }

        for (Map.Entry<Long, String> entry : winnerDescriptions.entrySet()) {
            Long winnerId = entry.getKey();
            String description = entry.getValue();

            Winner winner = winnerRepository.findById(winnerId)
                    .orElseThrow(() -> new RuntimeException("Winner with ID " + winnerId + " not found."));

            if ("PENDING_REVIEW".equals(winner.getStatus())) {
                winner.setDescription(description);
                winner.setStatus("PUBLISHED");
                winnerRepository.save(winner);
            }
        }
    }
}