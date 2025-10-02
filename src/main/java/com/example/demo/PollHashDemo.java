package com.example.demo;

import redis.clients.jedis.UnifiedJedis;
import java.util.LinkedHashMap;
import java.util.Map;

public class PollHashDemo {

    
    private static final String POLL_ID = "03ebcb7b-bd69-440b-924e-f5b7d664af7b";

    public static void main(String[] args) {
        String baseKey   = "poll:" + POLL_ID;
        String titleKey  = baseKey;                  
        String countsKey = baseKey + ":counts";      

        try (UnifiedJedis jedis = new UnifiedJedis("redis://localhost:6379")) {
            
            jedis.hset(titleKey, "title", "Pineapple on Pizza?");

            
            Map<String, String> initial = new LinkedHashMap<>();
            initial.put("0", "269");
            initial.put("1", "268");
            initial.put("2", "42");

            
            jedis.del(countsKey);
            jedis.hset(countsKey, initial);

            
            jedis.hincrBy(countsKey, "1", 1);

            
            String title = jedis.hget(titleKey, "title");
            Map<String, String> counts = jedis.hgetAll(countsKey);

            System.out.println("Title:  " + title);
            System.out.println("Counts: " + counts); 

            
            String opt1 = jedis.hget(countsKey, "1");
            long total  = counts.values().stream().mapToLong(Long::parseLong).sum();
            System.out.println("Option[1]: " + opt1);
            System.out.println("Total votes: " + total);
        }
    }
}
