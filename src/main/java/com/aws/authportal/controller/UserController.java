package com.aws.authportal.controller;

import com.aws.authportal.dtos.UserUpdateRequest;
import com.aws.authportal.entity.User;
import com.aws.authportal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestParam String userEmail, @RequestBody UserUpdateRequest updateRequest){
        String user = userService.updateUser(userEmail, updateRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam String userEmail){
        String user = userService.deleteUser(userEmail);
        return ResponseEntity.ok(user);
    }
}
