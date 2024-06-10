package com.example.treaders.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class UserFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 250)
    private String username;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(unique = true, nullable = false, length = 250)
    private String email;

    @Column(nullable = false, length = 250)
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
