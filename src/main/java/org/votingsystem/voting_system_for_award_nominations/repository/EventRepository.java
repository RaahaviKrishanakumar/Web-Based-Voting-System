package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    Optional<Event> findFirstByStatus(String status);
}
