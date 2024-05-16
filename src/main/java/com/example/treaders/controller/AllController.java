package com.example.treaders.controller;

import com.example.treaders.user.InputForm;
import com.example.treaders.LLM.LlamaService;
import com.example.treaders.videoFormat.VideoFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class AllController {
    private final JdbcClientRepository jdbcClientRepository;
    @Autowired
    private LlamaService llamaService;

    public AllController(JdbcClientRepository jdbcClientRepository) {
        this.jdbcClientRepository = jdbcClientRepository;
    }

    @GetMapping("/")
    public String Login(){
        return "login";
    }

    @PostMapping("/home")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model){
        if(jdbcClientRepository.authenticate(username, password)){
            List<VideoFormat> videos = jdbcClientRepository.getAllVideoPath();
            model.addAttribute("videos", videos);
            model.addAttribute("username", username);
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
        return "redirect:/";
    }

    @GetMapping("/chat")
    public String index(Model model) {
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

}
