package org.votingsystem.voting_system_for_award_nominations.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Winner;
import org.votingsystem.voting_system_for_award_nominations.repository.WinnerRepository;

import java.util.List;

@Service
public class PublicService {

    private final WinnerRepository winnerRepository;

    @Autowired
    public PublicService(WinnerRepository winnerRepository) {
        this.winnerRepository = winnerRepository;
    }

    /**
     * Fetches winners that are ready for public viewing.
     * The criteria are:
     * 1. The winner's status must be "PUBLISHED".
     * 2. The event the winner belongs to must have a status of "ACTIVE".
     * @return A list of published winners for the currently active event.
     */
    public List<Winner> getPublishedWinnersForActiveEvent() {
        // This query finds winners that meet both conditions
        return winnerRepository.findByEvent_StatusAndStatus("ACTIVE", "PUBLISHED");
    }
}