package com.example.treaders.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcClientRepository {
    private static final Logger log = LoggerFactory.getLogger(JdbcClientRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if(count == 0){
            return false;
        }
        sql = "SELECT password FROM user WHERE username = ?";
        String storedPassword = jdbcTemplate.queryForObject(sql, String.class, username);
        return storedPassword.equals(password);
    }

    public void addUser(String username, String password) {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, password);
    }

    public List<String> getAllVideoPath() {
        String sql = "SELECT file_path FROM videos";
        return jdbcTemplate.queryForList(sql,String.class);
    }
}
