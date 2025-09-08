package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // ren minnetilstand for hver test
class Assignment2RestClientIT {

    @LocalServerPort int port;
    RestClient client;

    @BeforeEach
    void setUp() {
        client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void endToEndScenario_withRestClient() {
        // 1) Create user 1 (Alice)
        Map<String, Object> user1 = post("/api/users", Map.of(
                "name", "Alice",
                "email", "alice@example.com"
        ), Map.class);
        Long user1Id = asLong(user1.get("id"));
        assertThat(user1Id).isNotNull();

        // 2) List users (contains Alice)
        List<?> users1 = getList("/api/users");
        assertThat(users1.size()).isGreaterThanOrEqualTo(1);

        // 3) Create user 2 (Bob)
        Map<String, Object> user2 = post("/api/users", Map.of(
                "name", "Bob",
                "email", "bob@example.com"
        ), Map.class);
        Long user2Id = asLong(user2.get("id"));
        assertThat(user2Id).isNotNull();

        // 4) List users (should be 2)
        List<?> users2 = getList("/api/users");
        assertThat(users2.size()).isGreaterThanOrEqualTo(2);

        // 5) User 1 creates a poll (two options)
        Map<String, Object> poll = post("/api/polls", Map.of(
                "question", "Best JS runtime?",
                "creatorId", user1Id,
                "options", List.of("Node.js", "Deno")
        ), Map.class);
        Long pollId = asLong(poll.get("id"));
        assertThat(pollId).isNotNull();

        List<Map<String, Object>> options = (List<Map<String, Object>>) poll.get("options");
        assertThat(options).hasSize(2);
        Long opt1Id = asLong(options.get(0).get("id"));
        Long opt2Id = asLong(options.get(1).get("id"));

        // 6) List polls (should include the new poll)
        List<?> polls = getList("/api/polls");
        assertThat(polls.size()).isGreaterThanOrEqualTo(1);

        // 7) User 2 votes on option 1
        Map<String, Object> vote1 = post("/api/votes", Map.of(
                "voterId", user2Id,
                "optionId", opt1Id
        ), Map.class);
        Map<String, Object> v1opt = (Map<String, Object>) vote1.get("option");
        assertThat(asLong(v1opt.get("id"))).isEqualTo(opt1Id);

        // 8) User 2 changes vote to option 2
        Map<String, Object> vote2 = post("/api/votes", Map.of(
                "voterId", user2Id,
                "optionId", opt2Id
        ), Map.class);
        Map<String, Object> v2opt = (Map<String, Object>) vote2.get("option");
        assertThat(asLong(v2opt.get("id"))).isEqualTo(opt2Id);

        // 9) List votes (should show only the most recent vote for user 2)
        List<Map<String, Object>> votes = getList("/api/votes");
        assertThat(votes).hasSize(1);
        Map<String, Object> v = votes.get(0);
        Map<String, Object> voter = (Map<String, Object>) v.get("voter");
        Map<String, Object> opt = (Map<String, Object>) v.get("option");
        assertThat(asLong(voter.get("id"))).isEqualTo(user2Id);
        assertThat(asLong(opt.get("id"))).isEqualTo(opt2Id);

        // 10) Delete the poll
        delete("/api/polls/" + pollId);

        // 11) List votes (should be empty)
        List<?> votesAfterDelete = getList("/api/votes");
        assertThat(votesAfterDelete).isEmpty();
    }

    // -----------------------------------
    // helpers
    // -----------------------------------
    private <T> T post(String path, Object body, Class<T> type) {
        return client.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(type);
    }

    private void delete(String path) {
        client.delete()
                .uri(path)
                .retrieve()
                .toBodilessEntity();
    }

    private List<Map<String, Object>> getList(String path) {
        // RestClient har ikke direkte "List.class" typeinfo; Map[] er en enkel hack
        Map[] arr = client.get()
                .uri(path)
                .retrieve()
                .body(Map[].class);
        return List.of(arr);
    }

    private static Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
}
