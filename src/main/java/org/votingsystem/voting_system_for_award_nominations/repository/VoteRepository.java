package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Vote;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // All votes for an event
    List<Vote> findByEvent_Id(Long eventId);

    // Delete all votes for an event (for restart)
    void deleteByEvent_Id(Long eventId);

    // Count votes grouped by nominee (only nominees who got votes)
    @Query("SELECT v.nominee.id, COUNT(v) " +
            "FROM Vote v " +
            "WHERE v.nominee.category.id = :categoryId " +
            "GROUP BY v.nominee.id")
    List<Object[]> countVotesByNominee(Long categoryId);

    //  Count votes including nominees with zero votes
    @Query("SELECT n.id, COUNT(v) " +
            "FROM Nominee n LEFT JOIN Vote v ON v.nominee.id = n.id " +
            "WHERE n.category.id = :categoryId " +
            "GROUP BY n.id")
    List<Object[]> countVotesByNomineeIncludingZero(Long categoryId);

    //  Check if a voter has already voted in a category
    boolean existsByNominee_Category_IdAndVoterId(Long categoryId, String voterId);
}
