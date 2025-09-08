# DAT250 Experimental Assignment 2 – Report

## Problems

### 1. Gradle and Project Setup
**Problem:** When trying to run the project with `gradle bootRun`, I got the error *“Directory does not contain a Gradle build”*. I realized I was running the command from the wrong directory. The correct way was to navigate into the Spring Boot project folder (`demo`) and run `.\gradlew.bat bootRun` (on Windows).  

### 2. OneDrive Path Issues
**Problem:** My project was located inside a OneDrive-synced folder (`C:\Users\Johan\OneDrive\Skrivebord\demo`). Sometimes this caused unexpected behavior with builds. I made sure always to run Gradle commands from the correct folder. I also avoided nested git repositories.

### 3. Infinite JSON Recursion
**Problem:** When serializing entities with bidirectional associations (`Poll` → `VoteOption` → `Poll` …), the JSON response contained endless nesting. I added Jackson annotations, e.g. `@JsonIgnore` on the `VoteOption.poll` field, which broke the recursion. This made the JSON responses clean and usable.

### 4. Testing with HTTP Client
**Problem:** Running multiple HTTP requests in VS Code produced duplicate data because I clicked them several times. I restarted the application (which resets the in-memory `PollManager`) and then executed the scenario step by step in the correct order.


## Pending Issues
- Currently, all data is only stored **in memory**. Restarting the application clears all polls, users, and votes. A persistent database (e.g. H2 or PostgreSQL) would be a natural next step.  
- API error handling is minimal. For example, invalid IDs currently may return generic errors instead of structured error messages.  
- The automated test uses `Map` and `List` for JSON parsing, which leads to warnings about “unchecked operations.” A cleaner solution would be to create proper DTO classes or use `ParameterizedTypeReference`.



