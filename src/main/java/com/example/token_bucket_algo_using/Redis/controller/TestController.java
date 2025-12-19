package com.example.token_bucket_algo_using.Redis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test(){
        return "Request Allowed";
    }
}
