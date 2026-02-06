package org.votingsystem.voting_system_for_award_nominations.modelentity;

import jakarta.persistence.*;

/**
 * Represents a single news article in the database.
 * It stores all possible fields for different news types.
 */
@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500) // A short summary for the card view
    private String summary;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private String type; // e.g., "STANDARD" or "EXTERNAL_LINK"

    @Lob // Use @Lob for potentially very long text content
    @Column(columnDefinition = "TEXT")
    private String content; // Full content for standard articles

    private String externalUrl; // For external link articles

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }
}