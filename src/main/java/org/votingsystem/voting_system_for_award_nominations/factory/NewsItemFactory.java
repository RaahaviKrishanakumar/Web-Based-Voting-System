package org.votingsystem.voting_system_for_award_nominations.factory;

import org.springframework.stereotype.Component;

@Component
public class NewsItemFactory {
    public NewsItem createNewsItem(String type, long id, String title, String imageUrl, String externalUrl, String summary) {
        if ("STANDARD".equalsIgnoreCase(type)) {
            return new StandardArticle(id, title, imageUrl, summary);
        } else if ("EXTERNAL_LINK".equalsIgnoreCase(type)) {
            return new ExternalLinkArticle(title, imageUrl, externalUrl, summary);
        }
        return null;
    }
}