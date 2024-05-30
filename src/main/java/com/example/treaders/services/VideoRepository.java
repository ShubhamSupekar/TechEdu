package com.example.treaders.services;

import com.example.treaders.models.VideoFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<VideoFormat,Integer> {
    List<VideoFormat> findByUploadedBy_Username(String username);
}
