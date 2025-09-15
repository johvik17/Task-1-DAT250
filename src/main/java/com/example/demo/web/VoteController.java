package com.example.demo.web;

import com.example.demo.domain.Vote;
import com.example.demo.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "http://localhost:5173") // Vite-dev
public class VoteController {
    private final PollManager pm;
    public VoteController(PollManager pm) { this.pm = pm; }

    @GetMapping public Iterable<Vote> all() { return pm.listVotes(); }

    @PostMapping
    public ResponseEntity<Vote> vote(@RequestBody Map<String, Long> body) {
        Long voterId = body.get("voterId");
        Long optionId = body.get("optionId");
        return ResponseEntity.ok(pm.castOrChangeVote(voterId, optionId)); 
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pm.deleteVote(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

