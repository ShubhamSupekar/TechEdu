package com.example.treaders.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;


@Controller
public class AllController {
    private final JdbcClientRepository jdbcClientRepository;

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
}
