package com.example.demo.web;

import com.example.demo.domain.User;
import com.example.demo.service.PollManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // Vite-dev
public class UserController {
    private final PollManager pm;
    public UserController(PollManager pm) { this.pm = pm; }

    @GetMapping public Iterable<User> all() { return pm.listUsers(); }

    @GetMapping("{id}")
    public ResponseEntity<User> one(@PathVariable Long id) {
        return pm.getUser(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody Map<String, String> body) {
        var u = pm.createUser(body.get("name"), body.get("email"));
        return ResponseEntity.created(URI.create("/api/users/" + u.getId())).body(u);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return pm.updateUser(id, body.get("name"), body.get("email"))
                 .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pm.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

