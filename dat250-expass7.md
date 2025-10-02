# DAT250 – Experiment Assignment 5

## Installation
I installed Redis using Docker (`redis:7-alpine`). After starting the container I tested with `redis-cli ping` and got `PONG`.

## CLI
- Tried out `SET`, `GET`, `EXPIRE`, and `TTL`.
- **Use case 1 (logged in users):** used `SADD`, `SREM`, and `SMEMBERS` to keep track of who was logged in/out.
- **Use case 2 (poll votes):** used `HSET` and `HINCRBY` to store poll options and increment votes without replacing everything.

## Java
- Added Jedis (`redis.clients:jedis:6.2.0`) to Gradle.
- Made `RedisSetDemo` to simulate logins with a Set.
- Made `PollHashDemo` to represent a poll with a Hash and update votes.

## Problems

- Gradle tried to build frontend stuff → solved by skipping frontend tasks and adding custom run tasks.
- `UnifiedJedis` not found at first → fixed by adding dependency and refreshing Gradle.

## Conclusion
Now I understand how Redis can be used as a key-value store, with Sets for sessions and Hashes for polls, and how to connect from Java with Jedis.
