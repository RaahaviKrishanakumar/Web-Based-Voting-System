package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Nominee;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Category;

import java.util.List;

@Repository
public interface NomineeRepository extends JpaRepository<Nominee, Long> {
    List<Nominee> findByCategory(Category category);

    List<Nominee> findByStatus(String status);

    //  All nominees of a given event
    List<Nominee> findByCategoryEventId(Long eventId);

    //  Nominees of a given event filtered by status (PENDING / APPROVED / REJECTED)
    List<Nominee> findByCategoryEventIdAndStatus(Long eventId, String status);

    List<Nominee> findByCategoryIdAndStatus(Long categoryId, String status);

}
