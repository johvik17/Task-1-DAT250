package com.example.demo.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String question;

    // Du har "creator" (beholdes)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User creator;

    // Du har "options" (beholdes)
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("presentationOrder ASC")
    private List<VoteOption> options = new ArrayList<>();

    protected Poll() {}

    
    public VoteOption addVoteOption(String caption) {
        VoteOption o = new VoteOption();
        o.setText(caption); 
        o.setPoll(this);
        o.setPresentationOrder(options.size()); 
        options.add(o);
        return o;
    }

    // Getters/setters
    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public List<VoteOption> getOptions() { return options; }
    public void setOptions(List<VoteOption> options) { this.options = options; }
}
