package org.votingsystem.voting_system_for_award_nominations.factory;

/**
 * A "Concrete Product" for a standard news article that links to an internal page.
 */
public class StandardArticle implements NewsItem {
    private long id;
    private String title;
    private String imageUrl;
    private String summary;

    public StandardArticle(long id, String title, String imageUrl, String summary) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.summary = summary;
    }

    @Override
    public String render() {
        return String.format(
                "<a href='/news/%d' style='text-decoration: none; color: inherit;'>" +
                        "  <img src='%s' class='card-img-top' alt='%s'>" +
                        "  <div class='card-body'>" +
                        "    <h5 class='card-title'>%s</h5>" +
                        "    <p class='card-text'>%s</p>" +
                        "  </div>" +
                        "</a>",
                id, imageUrl, title, title, summary
        );
    }
}