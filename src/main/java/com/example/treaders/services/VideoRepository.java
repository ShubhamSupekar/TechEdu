package com.example.treaders.services;

import com.example.treaders.models.VideoFormat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoFormat,Integer> {
}
