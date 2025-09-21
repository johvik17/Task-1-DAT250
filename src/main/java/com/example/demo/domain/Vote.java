package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Du har 'voter' (beholdes)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User voter;

    // Du har 'option' (beholdes)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private VoteOption option;

    protected Vote() {}

    // Getters/setters
    public Long getId() { return id; }
    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }
    public VoteOption getOption() { return option; }
    public void setOption(VoteOption option) { this.option = option; }
}
