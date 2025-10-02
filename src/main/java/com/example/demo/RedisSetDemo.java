package com.example.demo;

import redis.clients.jedis.UnifiedJedis;
import java.util.Set;

public class RedisSetDemo {
    public static void main(String[] args) {
        try (UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379")) {
            // Clear previous state
            jedis.del("session:loggedin");

            // alice logs in
            jedis.sadd("session:loggedin", "alice");

            // bob logs in
            jedis.sadd("session:loggedin", "bob");

            Set<String> now = jedis.smembers("session:loggedin");
            System.out.println("Logged in: " + now);   // [alice, bob]

            // alice logs off
            jedis.srem("session:loggedin", "alice");

            // eve logs in
            jedis.sadd("session:loggedin", "eve");

            System.out.println("Final: " + jedis.smembers("session:loggedin")); // [bob, eve]
            System.out.println("Count: " + jedis.scard("session:loggedin"));    // 2
        }
    }
}
