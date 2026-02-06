package org.votingsystem.voting_system_for_award_nominations.modelentity;

import jakarta.persistence.*;

@Entity
@Table(name = "voting_session")  // optional, matches DB table name
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to active event

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    private String status;   // PENDING, ONGOING, ENDED

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
