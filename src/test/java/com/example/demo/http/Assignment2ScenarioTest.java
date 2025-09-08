package com.example.demo.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class Assignment2ScenarioTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    void endToEndScenario() throws Exception {
        // --- 1) Create user 1 (Alice)
        MvcResult r1 = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name","Alice","email","alice@example.com"))))
                .andExpect(status().isCreated().or(status().isOk()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        JsonNode u1 = body(r1);
        long user1Id = u1.get("id").asLong();

        // --- 2) List users (should contain Alice)
        MvcResult r2 = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()).andReturn();
        JsonNode usersAfter1 = body(r2);
        assertThat(usersAfter1.isArray()).isTrue();
        assertThat(usersAfter1.size()).isGreaterThanOrEqualTo(1);

        // --- 3) Create user 2 (Bob)
        MvcResult r3 = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name","Bob","email","bob@example.com"))))
                .andExpect(status().isCreated().or(status().isOk()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        JsonNode u2 = body(r3);
        long user2Id = u2.get("id").asLong();

        // --- 4) List users (should be 2)
        MvcResult r4 = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()).andReturn();
        JsonNode usersAfter2 = body(r4);
        assertThat(usersAfter2.size()).isGreaterThanOrEqualTo(2);

        // --- 5) User 1 creates a poll with 2 options
        MvcResult r5 = mockMvc.perform(post("/api/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "question", "Best JS runtime?",
                                "creatorId", user1Id,
                                "options", List.of("Node.js", "Deno")
                        ))))
                .andExpect(status().isCreated().or(status().isOk()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        JsonNode poll = body(r5);
        long pollId = poll.get("id").asLong();
        JsonNode opts = poll.withArray("options");
        assertThat(opts.size()).isEqualTo(2);
        long opt1Id = opts.get(0).get("id").asLong();
        long opt2Id = opts.get(1).get("id").asLong();

        // --- 6) List polls (should include the new poll)
        MvcResult r6 = mockMvc.perform(get("/api/polls"))
                .andExpect(status().isOk()).andReturn();
        JsonNode polls = body(r6);
        assertThat(polls.isArray()).isTrue();
        assertThat(polls.size()).isGreaterThanOrEqualTo(1);

        // --- 7) User 2 votes for option 1
        MvcResult r7 = mockMvc.perform(post("/api/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("voterId", user2Id, "optionId", opt1Id))))
                .andExpect(status().isOk().or(status().isCreated()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        JsonNode vote1 = body(r7);
        assertThat(vote1.get("option").get("id").asLong()).isEqualTo(opt1Id);

        // --- 8) User 2 changes his vote to option 2
        MvcResult r8 = mockMvc.perform(post("/api/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("voterId", user2Id, "optionId", opt2Id))))
                .andExpect(status().isOk().or(status().isCreated()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        JsonNode vote2 = body(r8);
        assertThat(vote2.get("option").get("id").asLong()).isEqualTo(opt2Id);

        // --- 9) List votes (should show only Bob's most recent vote)
        MvcResult r9 = mockMvc.perform(get("/api/votes"))
                .andExpect(status().isOk()).andReturn();
        JsonNode votes = body(r9);
        assertThat(votes.isArray()).isTrue();
        assertThat(votes.size()).isEqualTo(1);
        JsonNode v = votes.get(0);
        assertThat(v.get("voter").get("id").asLong()).isEqualTo(user2Id);
        assertThat(v.get("option").get("id").asLong()).isEqualTo(opt2Id);

        // --- 10) Delete the poll
        mockMvc.perform(delete("/api/polls/{id}", pollId))
                .andExpect(status().isNoContent().or(status().isOk()));

        // --- 11) List votes (should be empty after poll deletion)
        MvcResult r11 = mockMvc.perform(get("/api/votes"))
                .andExpect(status().isOk()).andReturn();
        JsonNode votesAfterDelete = body(r11);
        assertThat(votesAfterDelete.isArray()).isTrue();
        assertThat(votesAfterDelete.size()).isEqualTo(0);
    }

    // ---- helpers ----
    private String json(Object o) throws Exception { return mapper.writeValueAsString(o); }
    private JsonNode body(MvcResult r) throws Exception { return mapper.readTree(r.getResponse().getContentAsByteArray()); }
}

