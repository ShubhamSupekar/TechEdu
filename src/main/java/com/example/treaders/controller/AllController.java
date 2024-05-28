package com.example.treaders.controller;

import com.example.treaders.models.VideoFormat;
import com.example.treaders.models.UserFormat;
import com.example.treaders.services.VideoRepository;
import com.example.treaders.user.InputForm;
import com.example.treaders.LLM.LlamaService;
import com.example.treaders.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.treaders.services.UserRepository;

import java.io.IOException;
import java.util.List;


@Controller
public class AllController {

    @Autowired
    private UserRepository UserRepo;

    @Autowired
    private VideoRepository VideoRepo;

    @Autowired
    private LlamaService llamaService;

    private boolean UserLoggedIn = false;

    private String UserName;

    @Autowired
    private VideoService videoService;


    @GetMapping("/")
    public String Login(){
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model){
        if(UserLoggedIn){
            List<VideoFormat> videos = VideoRepo.findAll();
            model.addAttribute("videos", videos);
            model.addAttribute("username", UserName);
            return "welcome";
        }
        return "redirect:/";
    }

    @PostMapping("/home")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model){
        UserFormat user = UserRepo.findByUsername(username);
        if(user!=null && user.getPassword().equals(password)){
            List<VideoFormat> videos = VideoRepo.findAll();
            model.addAttribute("videos", videos);
            model.addAttribute("username", username);
            UserName=username;
            UserLoggedIn = true;
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
        UserFormat user=new UserFormat();
        user.setUsername(username);
        user.setPassword(password);
        UserRepo.save(user);
        return "redirect:/";
    }


    @PostMapping("/logout")
    public String logout(){
        UserLoggedIn = false;
        UserName = "";
        return "redirect:/";
    }

    @GetMapping("/chat")
    public String index(Model model) {
        if(!UserLoggedIn){
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
        if(!UserLoggedIn){
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
