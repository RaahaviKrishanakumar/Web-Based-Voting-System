package org.votingsystem.voting_system_for_award_nominations.factory;

/**
 * The "Product" Interface. This is the common contract for all news items.
 */
public interface NewsItem {
    // This method will generate the specific HTML for the news card
    String render();
}