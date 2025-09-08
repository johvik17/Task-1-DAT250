package com.example.demo.domain;

public class Vote {
    private Long id;
    private User voter;
    private VoteOption option;

    public Vote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }

    public VoteOption getOption() { return option; }
    public void setOption(VoteOption option) { this.option = option; }
}

