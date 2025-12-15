package com.aws.authportal.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping
    public String get(){
        return "GET:: Admin API is working!";
    }

    @PostMapping
    public String post(){
        return "POST:: Admin API is working!";
    }

    @PutMapping
    public String put(){
        return "PUT:: Admin API is working!";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE:: Admin API is working!";
    }

}
