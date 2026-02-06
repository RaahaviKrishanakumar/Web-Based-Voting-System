package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);
    List<Feedback> findByEventIdAndUserId(Long eventId, Long userId);
}
