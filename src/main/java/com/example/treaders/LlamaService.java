package com.example.treaders;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Service;



@Service
public class LlamaService {

    private OllamaChatClient chatClient;

    public LlamaService(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getResponse(String input) {
        String response = chatClient.call(input);
        return response; // Simulated response
    }
}