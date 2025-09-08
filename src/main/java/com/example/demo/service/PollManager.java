package com.example.demo.service;

import com.example.demo.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class PollManager {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Poll> polls = new ConcurrentHashMap<>();
    private final Map<Long, VoteOption> options = new ConcurrentHashMap<>();
    private final Map<Long, Vote> votes = new ConcurrentHashMap<>();

    private final AtomicLong userSeq = new AtomicLong(1);
    private final AtomicLong pollSeq = new AtomicLong(1);
    private final AtomicLong optionSeq = new AtomicLong(1);
    private final AtomicLong voteSeq = new AtomicLong(1);

    public PollManager() {}

    
    public User createUser(String name, String email) {
        User u = new User();
        u.setId(userSeq.getAndIncrement());
        u.setName(name);
        u.setEmail(email);
        users.put(u.getId(), u);
        return u;
    }
    public Optional<User> getUser(Long id) { return Optional.ofNullable(users.get(id)); }
    public List<User> listUsers() { return new ArrayList<>(users.values()); }
    public Optional<User> updateUser(Long id, String name, String email) {
        User u = users.get(id);
        if (u == null) return Optional.empty();
        if (name != null) u.setName(name);
        if (email != null) u.setEmail(email);
        return Optional.of(u);
    }
    public boolean deleteUser(Long id) { return users.remove(id) != null; }

    
    public Poll createPoll(String question, Long creatorId, List<String> optionTexts) {
        User creator = users.get(creatorId);
        if (creator == null) throw new IllegalArgumentException("creatorId not found: " + creatorId);

        Poll p = new Poll();
        p.setId(pollSeq.getAndIncrement());
        p.setQuestion(question);
        p.setCreator(creator);

        if (optionTexts != null) {
            for (String txt : optionTexts) {
                VoteOption o = new VoteOption();
                o.setId(optionSeq.getAndIncrement());
                o.setText(txt);
                o.setPoll(p);
                options.put(o.getId(), o);
                p.getOptions().add(o);
            }
        }
        polls.put(p.getId(), p);
        return p;
    }
    public Optional<Poll> getPoll(Long id) { return Optional.ofNullable(polls.get(id)); }
    public List<Poll> listPolls() { return new ArrayList<>(polls.values()); }

    public Optional<Poll> updatePoll(Long pollId, String question, Long newCreatorId, List<String> replaceOptions) {
        Poll p = polls.get(pollId);
        if (p == null) return Optional.empty();
        if (question != null) p.setQuestion(question);
        if (newCreatorId != null) {
            User creator = users.get(newCreatorId);
            if (creator == null) throw new IllegalArgumentException("creatorId not found: " + newCreatorId);
            p.setCreator(creator);
        }
        if (replaceOptions != null) {
            for (VoteOption old : p.getOptions()) {
                votes.values().removeIf(v -> v.getOption() != null && Objects.equals(v.getOption().getId(), old.getId()));
                options.remove(old.getId());
            }
            p.getOptions().clear();
            for (String txt : replaceOptions) {
                VoteOption o = new VoteOption();
                o.setId(optionSeq.getAndIncrement());
                o.setText(txt);
                o.setPoll(p);
                options.put(o.getId(), o);
                p.getOptions().add(o);
            }
        }
        return Optional.of(p);
    }

    public boolean deletePoll(Long pollId) {
        Poll p = polls.remove(pollId);
        if (p == null) return false;
        for (VoteOption o : p.getOptions()) {
            votes.values().removeIf(v -> v.getOption() != null && Objects.equals(v.getOption().getId(), o.getId()));
            options.remove(o.getId());
        }
        return true;
    }

    
    public Optional<VoteOption> getOption(Long optionId) { return Optional.ofNullable(options.get(optionId)); }
    public VoteOption addOptionToPoll(Long pollId, String text) {
        Poll p = polls.get(pollId);
        if (p == null) throw new IllegalArgumentException("pollId not found: " + pollId);
        VoteOption o = new VoteOption();
        o.setId(optionSeq.getAndIncrement());
        o.setText(text);
        o.setPoll(p);
        options.put(o.getId(), o);
        p.getOptions().add(o);
        return o;
    }
    public boolean removeOption(Long optionId) {
        VoteOption o = options.remove(optionId);
        if (o == null) return false;
        votes.values().removeIf(v -> v.getOption() != null && Objects.equals(v.getOption().getId(), optionId));
        Poll p = o.getPoll();
        if (p != null) p.getOptions().removeIf(opt -> Objects.equals(opt.getId(), optionId));
        return true;
    }

    
    public Vote castOrChangeVote(Long voterId, Long optionId) {
        User voter = users.get(voterId);
        VoteOption option = options.get(optionId);
        if (voter == null) throw new IllegalArgumentException("voterId not found: " + voterId);
        if (option == null) throw new IllegalArgumentException("optionId not found: " + optionId);

        Long pollId = option.getPoll() != null ? option.getPoll().getId() : null;
        if (pollId != null) {
            votes.values().removeIf(v ->
                v.getVoter() != null
                    && Objects.equals(v.getVoter().getId(), voterId)
                    && v.getOption() != null
                    && v.getOption().getPoll() != null
                    && Objects.equals(v.getOption().getPoll().getId(), pollId)
            );
        }

        Vote vote = new Vote();
        vote.setId(voteSeq.getAndIncrement());
        vote.setVoter(voter);
        vote.setOption(option);
        votes.put(vote.getId(), vote);
        return vote;
    }
    public boolean deleteVote(Long voteId) { return votes.remove(voteId) != null; }
    public List<Vote> listVotes() { return new ArrayList<>(votes.values()); }

    // ---------- Utility ----------
    public long countVotesForOption(Long optionId) {
        return votes.values().stream().filter(v -> v.getOption() != null && Objects.equals(v.getOption().getId(), optionId)).count();
    }
    public Map<Long, Long> tallyForPoll(Long pollId) {
        Poll p = polls.get(pollId);
        if (p == null) throw new IllegalArgumentException("pollId not found: " + pollId);
        Map<Long, Long> counts = new LinkedHashMap<>();
        for (VoteOption o : p.getOptions()) counts.put(o.getId(), countVotesForOption(o.getId()));
        return counts;
    }
}

