package com.aws.authportal.service;

import com.aws.authportal.dtos.SalaryResponse;
import com.aws.authportal.dtos.SearchSalaryRequest;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.UserRepository;
import com.aws.authportal.specification.SalarySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;

    public String updateSalary(String userEmail, long newSalary) {
        Optional<User> user = userRepository.findByEmail(userEmail);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication principal: {}", authentication.getPrincipal());
        User currentUser = (User) authentication.getPrincipal(); // user yang sedang login

        if (user.isPresent()) {

            if (!String.valueOf(currentUser.getRole()).equalsIgnoreCase("ADMIN")) {
                throw new RuntimeException("Unauthorized: Only ADMIN can update salary");
            }
            User targetUser = user.get();
            targetUser.setSalary(newSalary);
            User save = userRepository.save(targetUser);
            return "Salary updated successfully for user: " + save.getFullName()  + " with new salary: " + save.getSalary();
        } else {
            throw (new RuntimeException("User not found"));
        }
    }

    public SalaryResponse getSalaryByEmail(String userEmail) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Cek dulu user target ada atau tidak
        User targetUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = currentUser.getRole().name().equalsIgnoreCase("ADMIN");

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized: Only ADMIN can view salary info");
        }

        return SalaryResponse.builder()
                .fullName(targetUser.getFullName())
                .role(targetUser.getRole().name())
                .salary(targetUser.getSalary())
                .build();
    }

    public Page<User> getAllStaffSalary(SearchSalaryRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        boolean isAdmin = currentUser.getRole().name().equalsIgnoreCase("ADMIN");

        if (!isAdmin) {
            throw new RuntimeException("Unauthorized: Only ADMIN can view salary info");
        }

        if(request.getPage() <= 0){
            request.setPage(1);
        }

        String validSortBy;
        if("fullName".equalsIgnoreCase(request.getSortBy()) || "role".equalsIgnoreCase(request.getSortBy())){
            validSortBy = request.getSortBy();
        } else {
            validSortBy = "id"; // default sort by id
        }

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), validSortBy);

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        Specification<User> specification = SalarySpecification.getSpecification(request);

        return userRepository.findAll(specification, pageable);
    }
}
