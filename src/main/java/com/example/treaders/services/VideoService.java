package com.example.treaders.services;

import com.example.treaders.models.VideoFormat;
import com.example.treaders.models.UserFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository UserRepo;

    private UserFormat userFormat;
//    private final JdbcClientRepository jdbcClientRepository;
//
//    @Autowired
//    public VideoService(JdbcClientRepository jdbcClientRepository) {
//        this.jdbcClientRepository = jdbcClientRepository;
//    }

    public void saveVideo(MultipartFile file, String title, String description, String UserName) throws IOException {
        // Save the uploaded video file to a directory
        String filePath = saveVideoFile(file);

        UserFormat user = UserRepo.findByUsername(UserName);

        if(user==null){
            throw new IllegalArgumentException("User not found "+UserName);
        }

        // Save video details to the database
        VideoFormat videoFormat = new VideoFormat();
        videoFormat.setTitle(title);
        videoFormat.setDescription(description);
        videoFormat.setFilepath(filePath);
        videoFormat.setUploadedBy(user);
        videoFormat.setUploadedAt(LocalDateTime.now());
        videoRepository.save(videoFormat);
    }

    private String saveVideoFile(MultipartFile file) throws IOException {
        // Directory where videos will be saved
        String uploadDir = "public/Videos/";

        // Create the directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file to the directory
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Return the file name
        return fileName;
    }
}