package com.example.treaders.controller;

import com.example.treaders.user.InputForm;
import com.example.treaders.LLM.LlamaService;
import com.example.treaders.videoFormat.VideoFormat;
import com.example.treaders.videoFormat.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
public class AllController {
    private final JdbcClientRepository jdbcClientRepository;
    @Autowired
    private LlamaService llamaService;
    private boolean UserLoiggedIn = false;
    private String UserName;
    @Autowired
    private VideoService videoService;

    public AllController(JdbcClientRepository jdbcClientRepository) {
        this.jdbcClientRepository = jdbcClientRepository;
    }

    @GetMapping("/")
    public String Login(){
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model){
        if(UserLoiggedIn){
            List<VideoFormat> videos = jdbcClientRepository.getAllVideoPath();
            model.addAttribute("videos", videos);
            model.addAttribute("username", UserName);
            return "welcome";
        }
        return "redirect:/";
    }

    @PostMapping("/home")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model){
        if(jdbcClientRepository.authenticate(username, password)){
            List<VideoFormat> videos = jdbcClientRepository.getAllVideoPath();
            model.addAttribute("videos", videos);
            model.addAttribute("username", username);
            UserName=username;
            UserLoiggedIn = true;
            return "welcome";
        }else{
            return "redirect:/signup";
        }
    }

    @GetMapping("/signup")
    public String Signup(){
        return "signup";
    }

    @PostMapping("/newuser")
    public String addNewUser(@RequestParam String username, @RequestParam String password){
        jdbcClientRepository.addUser(username, password);
        return "redirect:/";
    }
    @PostMapping("/logout")
    public String logout(){
        UserLoiggedIn = false;
        UserName = "";
        return "redirect:/";
    }

    @GetMapping("/chat")
    public String index(Model model) {
        if(!UserLoiggedIn){
            return "redirect:/";
        }
        model.addAttribute("inputForm", new InputForm());
        return "ChatPage";
    }

    @PostMapping("/chat")
    public String processString(InputForm inputForm,Model model) {
        String userInput = inputForm.getInputString();
        String llamaResponse = llamaService.getResponse(userInput); // Get the response from Llama
        inputForm.setResponseString(llamaResponse); // Set the response in the inputForm
        model.addAttribute("inputForm", inputForm); // Update the model with the inputForm containing the response
        return "ChatPage";
    }

    @GetMapping("/upload")
    public String getUploadVideoFormat(){
        if(!UserLoiggedIn){
            return "redirect:/";
        }
        return "UploadVideo";
    }

    @PostMapping("/upload")
    public String UploadVideo(@RequestParam("file") MultipartFile file,
                            @RequestParam("title") String title,
                              @RequestParam("description") String description){
        try {
            // Check if the uploaded file is not empty
            if (file.isEmpty()) {
                return "redirect:/uploadForm?error=fileEmpty";
            }

            // Validate the file type (optional)
            if (!file.getContentType().startsWith("video/")) {
                return "redirect:/uploadForm?error=invalidFileType";
            }

            // Save the video
            videoService.saveVideo(file, title, description,UserName);

            // Redirect to a success page
            return "redirect:/home";
        } catch (IOException e) {
            // Handle file I/O errors
            return "redirect:/uploadForm?error=fileError";
        }
    }

}
