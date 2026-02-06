package org.votingsystem.voting_system_for_award_nominations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.votingsystem.voting_system_for_award_nominations.modelentity.News;
import java.util.List;

/**
 * Repository interface for News entities.
 * Handles database operations for news articles.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // Custom method to fetch all news, ordered by newest first
    List<News> findAllByOrderByIdDesc();
}