package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.VotingSession;

import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    //  Get the most recent session for an event
    Optional<VotingSession> findTopByEvent_IdOrderByIdDesc(Long eventId);
}
