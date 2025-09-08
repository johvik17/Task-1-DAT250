package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VoteOption {
    private Long id;
    private String text;

    @JsonIgnore           
    private Poll poll;

    public VoteOption() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) { this.poll = poll; }
}
