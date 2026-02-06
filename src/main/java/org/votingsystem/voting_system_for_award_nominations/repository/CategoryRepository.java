package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByEvent(Event event);
    List<Category> findByEventId(Long eventId);

}
