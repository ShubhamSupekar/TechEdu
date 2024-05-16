package com.example.treaders.controller;

import com.example.treaders.videoFormat.VideoFormat;
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

    public List<VideoFormat> getAllVideoPath() {
        String sql = "SELECT v.title, v.description, v.file_path, u.username " +
                "FROM videos v " +
                "INNER JOIN user u ON v.uploaded_by = u.id";
        return jdbcTemplate.query(sql,(vs,rowNum) -> new  VideoFormat(
                vs.getString("title"),
                vs.getString("description"),
                vs.getString("file_path"),
                vs.getString("username")
        ));
    }
}
