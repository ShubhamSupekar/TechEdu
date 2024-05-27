package com.example.treaders.videoFormat;

import com.example.treaders.DataBaseConnection.JdbcClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class VideoService {

    private final JdbcClientRepository jdbcClientRepository;

    @Autowired
    public VideoService(JdbcClientRepository jdbcClientRepository) {
        this.jdbcClientRepository = jdbcClientRepository;
    }

    public void saveVideo(MultipartFile file, String title, String description, String uploadedBy) throws IOException {
        // Save the uploaded video file to a directory
        String filePath = saveVideoFile(file);

        // Save video details to the database
        jdbcClientRepository.saveVideoIntoDatabase(title, description, filePath, uploadedBy);
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