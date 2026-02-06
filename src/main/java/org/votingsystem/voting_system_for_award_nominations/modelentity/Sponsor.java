package org.votingsystem.voting_system_for_award_nominations.modelentity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sponsor")
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String name;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;



    // Relative URL to serve from static/ or controller (e.g. /uploads/sponsors/xxx.pdf)
    private String agreementFilePath;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }


    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }



    public String getAgreementFilePath() { return agreementFilePath; }
    public void setAgreementFilePath(String agreementFilePath) { this.agreementFilePath = agreementFilePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
