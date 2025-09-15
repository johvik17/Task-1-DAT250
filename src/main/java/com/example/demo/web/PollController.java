package com.example.demo.web;

import com.example.demo.domain.Poll;
import com.example.demo.domain.VoteOption;
import com.example.demo.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/polls")
@CrossOrigin(origins = "http://localhost:5173")
public class PollController {

    private final PollManager pm;

    public PollController(PollManager pm) { this.pm = pm; }

    // ---- DTOs ----
    public record CreatePollRequest(String question, List<String> options, Long creatorId) {}
    public record VoteRequest(Long optionId) {}
    public record OptionDto(Long id, String text, Integer votes) {}
    public record PollDto(Long id, String question, List<OptionDto> options) {}

    // ---- Helpers ----
    private Long ensureDefaultUser() {
        var users = pm.listUsers();
        if (users.isEmpty()) {
            return pm.createUser("demo", "demo@example.com").getId();
        }
        return users.get(0).getId();
    }

    private PollDto toDto(Poll p) {
        Map<Long, Long> tallies = pm.tallyForPoll(p.getId()); // optionId -> count
        List<OptionDto> opts = new ArrayList<>();
        for (VoteOption o : p.getOptions()) {
            int votes = tallies.getOrDefault(o.getId(), 0L).intValue();
            opts.add(new OptionDto(o.getId(), o.getText(), votes));
        }
        return new PollDto(p.getId(), p.getQuestion(), opts);
    }

    // ---- Endpoints ----
    @GetMapping
    public ResponseEntity<List<PollDto>> all() {
        List<PollDto> out = new ArrayList<>();
        for (Poll p : pm.listPolls()) out.add(toDto(p));
        return ResponseEntity.ok(out);
    }

    @GetMapping("{id}")
    public ResponseEntity<PollDto> one(@PathVariable Long id) {
        return pm.getPoll(id).map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
public ResponseEntity<PollDto> create(@RequestBody CreatePollRequest body) {
    if (body == null || body.question() == null || body.question().isBlank())
        return ResponseEntity.badRequest().build();

    List<String> raw = body.options() == null
            ? Collections.<String>emptyList()
            : body.options();

    List<String> opts = raw.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());

    if (opts.size() < 2) return ResponseEntity.badRequest().build();

    Long creatorId = body.creatorId() != null ? body.creatorId() : ensureDefaultUser();
    Poll p = pm.createPoll(body.question().trim(), creatorId, opts);
    return ResponseEntity.created(URI.create("/api/polls/" + p.getId())).body(toDto(p));
}


    @PostMapping("{id}/vote")
    public ResponseEntity<PollDto> vote(@PathVariable Long id, @RequestBody VoteRequest body) {
        if (body == null || body.optionId() == null) return ResponseEntity.badRequest().build();
        Long voterId = ensureDefaultUser();            // use a default voter so frontend doesn’t need to send it
        pm.castOrChangeVote(voterId, body.optionId()); // uses your PollManager
        return pm.getPoll(id).map(p -> ResponseEntity.ok(toDto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pm.deletePoll(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
