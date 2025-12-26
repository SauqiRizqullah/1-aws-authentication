package com.aws.authportal.service;


import com.aws.authportal.dtos.UserUpdateRequest;
import com.aws.authportal.entity.DayOff;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public String updateUser(String userEmail, UserUpdateRequest updateRequest) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            log.info("User with email {} not found for update.", userEmail);
            throw new RuntimeException("User not found");
        }

        User targetUser = userOpt.get(); // user yang mau diupdate

        // Ambil user yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication principal: {}", authentication.getPrincipal());
        User currentUser = (User) authentication.getPrincipal(); // user yang sedang login

        boolean isAdmin = String.valueOf(currentUser.getRole()).equalsIgnoreCase("ADMIN");
        boolean isSelf = String.valueOf(currentUser.getEmail()).equalsIgnoreCase(userEmail);

        // Rule Validation
        if (!isAdmin && !isSelf){
            throw new RuntimeException("Unauthorized: You cannot update other users");
        }

        // Lanjut update
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()){
            targetUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        if(updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isBlank()){
            targetUser.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        User savedUser = userRepository.save(targetUser);

        return "User named " + savedUser.getFullName() + " has been updated.";
    }

    public String deleteUser(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            log.info("User with email {} not found for update.", userEmail);
            throw new RuntimeException("User not found");
        }

        User targetUser = userOpt.get();

        // Ambil user yang sedang login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication principal: {}", authentication.getPrincipal());
        User currentUser = (User) authentication.getPrincipal(); // user yang sedang login

        boolean isAdmin = String.valueOf(currentUser.getRole()).equalsIgnoreCase("ADMIN");

        // Rule Validation
        if (!isAdmin){
            throw new RuntimeException("Unauthorized: You cannot delete users unless you are an admin");
        }

        if (String.valueOf(currentUser.getEmail()).equalsIgnoreCase(userEmail)){
            throw new RuntimeException("Unauthorized: You cannot delete yourself");
        }

        userRepository.delete(targetUser);

        return "User named " + targetUser.getFullName() + " has been deleted.";

    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // ✅ NEW: flexible search (recommended)
    public List<User> findByFullNameContains(String fullName) {
        log.info("Searching users by fullName contains: {}", fullName);
        return userRepository.findByFullNameContainingIgnoreCase(fullName);
    }
}
