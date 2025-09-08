package com.example.demo.web;

import com.example.demo.domain.Poll;
import com.example.demo.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollManager pm;
    public PollController(PollManager pm) { this.pm = pm; }

    @GetMapping public Iterable<Poll> all() { return pm.listPolls(); }

    @GetMapping("{id}")
    public ResponseEntity<Poll> one(@PathVariable Long id) {
        return pm.getPoll(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Poll> create(@RequestBody Map<String, Object> body) {
        String question = (String) body.get("question");
        Long creatorId = ((Number) body.get("creatorId")).longValue();
        @SuppressWarnings("unchecked")
        List<String> options = (List<String>) body.get("options");
        var p = pm.createPoll(question, creatorId, options);
        return ResponseEntity.created(URI.create("/api/polls/" + p.getId())).body(p);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pm.deletePoll(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

