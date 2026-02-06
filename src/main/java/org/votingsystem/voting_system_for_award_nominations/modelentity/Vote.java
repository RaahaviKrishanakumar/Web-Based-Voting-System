package org.votingsystem.voting_system_for_award_nominations.modelentity;

import jakarta.persistence.*;

@Entity
@Table(name = "vote")   // optional, keeps table name consistent
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who voted (optional if anonymous)
    private String voterId;

    private int age;
    private String country;

    // Link to nominee
    @ManyToOne
    @JoinColumn(name = "nominee_id", nullable = false)
    private Nominee nominee;

    // Link to event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getVoterId() {
        return voterId;
    }
    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public Nominee getNominee() {
        return nominee;
    }
    public void setNominee(Nominee nominee) {
        this.nominee = nominee;
    }

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
}
