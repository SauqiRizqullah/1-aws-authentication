package com.aws.authportal.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management")
public class DemoManagementController {

    @GetMapping
    public String get(){
        return "GET:: Management API is working!";
    }

    @PostMapping
    public String post(){
        return "POST:: Management API is working!";
    }

    @PutMapping
    public String put(){
        return "PUT:: Management API is working!";
    }

    @DeleteMapping
    public String delete(){
        return "DELETE:: Management API is working!";
    }
}
