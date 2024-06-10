package com.example.treaders.controller;

import com.example.treaders.models.VideoFormat;
import com.example.treaders.models.UserFormat;
import com.example.treaders.services.VideoRepository;
import com.example.treaders.LLM.LlamaService;
import com.example.treaders.services.VideoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.treaders.services.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AllController {

    @Autowired
    private UserRepository UserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VideoRepository VideoRepo;

    @Autowired
    private LlamaService llamaService;

    @Autowired
    private VideoService videoService;

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        UserFormat user = UserRepo.findByEmail(email);
        if (user == null) {
            model.addAttribute("errorEmailMessage", "Email doesn't exist");
            return "login";
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            session.setAttribute("username", user.getUsername());
            return "redirect:/home";
        } else {
            model.addAttribute("errorPasswordMessage", "Wrong password");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "filter", required = false) String filter, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            List<VideoFormat> videos;
            if ("myvideos".equals(filter)) {
                videos = VideoRepo.findByUploadedBy_Username(username);
            } else {
                videos = VideoRepo.findAll();
            }
            model.addAttribute("videos", videos);
            model.addAttribute("username", username);
            return "welcome";
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/newuser")
    public String addNewUser(@RequestParam String email, @RequestParam String username, @RequestParam String password, Model model) {
        if (UserRepo.findByEmail(email) != null) {
            model.addAttribute("errorEmailMessage", "This email already exists");
            return "signup";
        }
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#]).+$";
        if (!password.matches(regex)) {
            model.addAttribute("errorPasswordMessage", "Password must be at least 6 characters long and include at least one capital letter and one special character (@, #, or $).");
            model.addAttribute("email", email);
            model.addAttribute("username", username);
            return "signup";
        }
        UserFormat user = new UserFormat();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        UserRepo.save(user);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/chat")
    public String showChatPage(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/";
        }
        return "ChatPage";
    }

    @GetMapping("/upload")
    public String getUploadVideoFormat(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/";
        }
        return "UploadVideo";
    }

    @PostMapping("/upload")
    public String uploadVideo(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        try {
            if (file.isEmpty()) {
                return "redirect:/uploadForm?error=fileEmpty";
            }
            if (!file.getContentType().startsWith("video/")) {
                return "redirect:/uploadForm?error=invalidFileType";
            }
            videoService.saveVideo(file, title, description, username);
            return "redirect:/home";
        } catch (IOException e) {
            return "redirect:/uploadForm?error=fileError";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePage(@PathVariable("id") int id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        try {
            VideoFormat video = VideoRepo.findById(id).get();
            Path videoPath = Paths.get("public/Videos/" + video.getFilepath());
            Files.deleteIfExists(videoPath);
            VideoRepo.deleteById(id);
        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        }
        return "redirect:/home";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable("id") int id, Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        VideoFormat video = VideoRepo.findById(id).get();
        model.addAttribute("video", video);
        return "editPage";
    }

    @PostMapping("/edit/{id}")
    public String editVideoDetails(@PathVariable("id") int id,
                                   @RequestParam("title") String title,
                                   @RequestParam("description") String description,
                                   @RequestParam("file") MultipartFile file,
                                   HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        try {
            VideoFormat video = VideoRepo.findById(id).get();
            if (file != null && !file.isEmpty()) {
                Path oldVideoPath = Paths.get("public/Videos/" + video.getFilepath());
                Files.deleteIfExists(oldVideoPath);
                String fileName = "_" + file.getOriginalFilename();
                video.setFilepath(fileName);
                Path uploadPath = Paths.get("public/Videos/");
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
            }
            video.setTitle(title);
            video.setDescription(description);
            VideoRepo.save(video);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/home";
    }
}
