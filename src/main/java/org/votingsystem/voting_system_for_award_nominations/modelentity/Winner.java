package org.votingsystem.voting_system_for_award_nominations.modelentity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Winner nominee
    @ManyToOne
    @JoinColumn(name = "nominee_id", nullable = false)
    private Nominee nominee;

    // Link to event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String imagePath;   // uploaded winner image

    // === NEW FIELDS START ===

    /**
     * A description of the winner's achievement, added by the PRO before publication.
     * The @Lob annotation indicates that this can store a large amount of text.
     */
    @Lob
    private String description;

    /**
     * The current state of the winner announcement.
     * It can be "DECLARED", "PENDING_REVIEW", or "PUBLISHED".
     * It defaults to "DECLARED" when a new winner is created.
     */
    @Column(nullable = false)
    private String status = "DECLARED";

    // === NEW FIELDS END ===
}