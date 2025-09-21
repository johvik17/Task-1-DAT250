package com.example.demo.domain;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users") // for å matche native query i testen under
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Du brukte "name" før; vi beholder det (testen vår bruker "name")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Praktisk: å ha tilbakekobling til polls opprettet av bruker
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Poll> created = new LinkedHashSet<>();

    protected User() {}

    /** Oppgavens ønskede ctor finnes ofte: praktisk å ha */
    public User(String username, String email) {
        this.name = username;
        this.email = email;
    }

    /** Oppgavens helper-metode */
    public Poll createPoll(String question) {
        Poll p = new Poll();
        p.setQuestion(question);
        p.setCreator(this);
        this.created.add(p);
        return p;
    }

    /** Oppgavens helper-metode */
    public Vote voteFor(VoteOption option) {
        Vote v = new Vote();
        v.setOption(option);
        v.setVoter(this);
        return v;
    }

    // Getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Poll> getCreated() { return created; }
}
