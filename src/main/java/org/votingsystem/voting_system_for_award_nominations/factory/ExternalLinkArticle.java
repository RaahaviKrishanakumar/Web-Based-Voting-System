package org.votingsystem.voting_system_for_award_nominations.factory;

/**
 * A "Concrete Product" for a news item that links to an external site.
 */
public class ExternalLinkArticle implements NewsItem {
    private String title;
    private String imageUrl;
    private String externalUrl;
    private String summary;

    public ExternalLinkArticle(String title, String imageUrl, String externalUrl, String summary) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.externalUrl = externalUrl;
        this.summary = summary;
    }

    @Override
    public String render() {
        return String.format(
                "<a href='%s' target='_blank' style='text-decoration: none; color: inherit;'>" +
                        "  <img src='%s' class='card-img-top' alt='%s'>" +
                        "  <div class='card-body'>" +
                        "    <h5 class='card-title'>%s</h5>" +
                        "    <p class='card-text'>%s</p>" +
                        "  </div>" +
                        "</a>",
                externalUrl, imageUrl, title, title, summary
        );
    }
}