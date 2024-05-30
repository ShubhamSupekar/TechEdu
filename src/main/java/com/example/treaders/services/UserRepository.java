package com.example.treaders.services;

import com.example.treaders.models.UserFormat;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<UserFormat, Integer> {
    UserFormat findByUsername(String username);
    UserFormat findByEmail(String email);
}
