package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Du har 'text' (beholdes). Testen vår bruker dette navnet.
    @Column(nullable=false)
    private String text;

    // Nytt felt: trengs for ordering/queries i testen
    @Column(nullable=false)
    private int presentationOrder;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Poll poll;

    protected VoteOption() {}

    // Getters/setters
    public Long getId() { return id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public int getPresentationOrder() { return presentationOrder; }
    public void setPresentationOrder(int presentationOrder) { this.presentationOrder = presentationOrder; }
    public Poll getPoll() { return poll; }
    public void setPoll(Poll poll) { this.poll = poll; }
}
