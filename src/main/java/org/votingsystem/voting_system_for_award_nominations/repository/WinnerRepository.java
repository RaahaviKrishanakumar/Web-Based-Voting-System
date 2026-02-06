package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Winner;
import java.util.List;

public interface WinnerRepository extends JpaRepository<Winner, Long> {

    // These methods likely already exist
    boolean existsByEvent_IdAndCategory_Id(Long eventId, Long categoryId);
    List<Winner> findByEvent_Id(Long eventId);

    // === NEW METHODS START ===

    /** Finds winners for a specific event that have a certain status. */
    List<Winner> findByEvent_IdAndStatus(Long eventId, String status);

    /** Finds all winners with a specific status, regardless of the event. */
    List<Winner> findByStatus(String status);

    /** Finds winners for the public view: must be PUBLISHED and from an ACTIVE event. */
    List<Winner> findByEvent_StatusAndStatus(String eventStatus, String winnerStatus);

    // === NEW METHODS END ===
}