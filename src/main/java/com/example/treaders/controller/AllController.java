package com.example.treaders.controller;

import com.example.treaders.models.VideoFormat;
import com.example.treaders.models.UserFormat;
import com.example.treaders.services.VideoRepository;
import com.example.treaders.user.InputForm;
import com.example.treaders.LLM.LlamaService;
import com.example.treaders.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.treaders.services.UserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


//Sanjay@123

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

    private boolean UserLoggedIn = false;

    private String UserName;

    @Autowired
    private VideoService videoService;


    @GetMapping("/")
    public String Login(){
        return "login";
    }


    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String email, @RequestParam String password){
        UserFormat user = UserRepo.findByEmail(email);
        if(user!=null && passwordEncoder.matches(password,user.getPassword())){
            UserLoggedIn = true;
            UserName = user.getUsername();
            return "redirect:/home";
        }else{
            return "redirect:/signup";
        }
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "filter", required = false) String filter, Model model) {
        if (UserLoggedIn) {
            List<VideoFormat> videos;
            if ("myvideos".equals(filter)) {
                videos = VideoRepo.findByUploadedBy_Username(UserName);
            } else {
                videos = VideoRepo.findAll();
            }
            model.addAttribute("videos", videos);
            model.addAttribute("username", UserName); // Add the username to the model
            return "welcome";
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String Signup(){
        return "signup";
    }

    @PostMapping("/newuser")
    public String addNewUser(@RequestParam String email,@RequestParam String username, @RequestParam String password,Model model){
        UserFormat user=new UserFormat();
        user.setEmail(email);
        user.setUsername(username);
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#]).+$";
        if(!password.matches(regex)){
            model.addAttribute("errorMessage", "Password must be at least 6 characters long and include at least one capital letter and one special character (@, #, or $).");
            model.addAttribute("email", email);
            model.addAttribute("username", username);
            return "signup";
        }
        user.setPassword(passwordEncoder.encode(password));
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
        if(!UserLoggedIn){
            return "redirect:/";
        }
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

    @GetMapping("/delete/{id}")
    public String detelePage(@PathVariable("id") int id){
        if(!UserLoggedIn){
            return "redirect:/";
        }
        try {
            VideoFormat video = VideoRepo.findById(id).get();
            Path videoPath = Paths.get("public/Videos/" + video.getFilepath());
            try {
                Files.delete(videoPath);
            } catch (Exception e) {
                System.out.println("Exception" + e.getMessage());
            }
            VideoRepo.deleteById(id);
        }catch (Exception e){
            System.out.println("Exception" + e.getMessage());
        }
        return "redirect:/home";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable("id") int id, Model model){
        if(!UserLoggedIn){
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
                                   @RequestParam("file") MultipartFile file
                                   ){
        if(!UserLoggedIn){
            return "redirect:/";
        }
        try {
            VideoFormat video = VideoRepo.findById(id).get();
            if (file != null && !file.isEmpty()) {
                Path OldVideoPath = Paths.get("public/Videos/" + video.getFilepath());
                try {
                    Files.delete(OldVideoPath);
                    String fileName = "_"+file.getOriginalFilename();
                    video.setFilepath(fileName);
                    Path uploadPath = Paths.get("public/Videos/");
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath,file.getBytes());
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            }
            video.setTitle(title);
            video.setDescription(description);
            VideoRepo.save(video);
        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/home";
    }

}
