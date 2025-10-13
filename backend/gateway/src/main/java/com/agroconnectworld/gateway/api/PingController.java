package com.agroconnectworld.gateway.api;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PingController {

    private final JdbcTemplate jdbc;
    private final StringRedisTemplate redis;

    public PingController(JdbcTemplate jdbc, StringRedisTemplate redis) {
        this.jdbc = jdbc;
        this.redis = redis;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> out = new HashMap<>();

        // Postgres: SELECT 1
        try {
            Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
            out.put("postgres", one != null && one == 1 ? "OK" : "FAIL");
        } catch (DataAccessException e) {
            out.put("postgres", "ERROR: " + e.getMessage());
        }

        // Redis: set/get simple key
        try {
            String key = "ping";
            String val = "pong";
            redis.opsForValue().set(key, val);
            String back = redis.opsForValue().get(key);
            out.put("redis", val.equals(back) ? "OK" : "FAIL");
        } catch (Exception e) {
            out.put("redis", "ERROR: " + e.getMessage());
        }

        out.put("status", "UP");
        return out;
    }
}
