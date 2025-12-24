package com.aws.authportal.controller;

import com.aws.authportal.dtos.CommonResponse;
import com.aws.authportal.dtos.PagingResponse;
import com.aws.authportal.dtos.SalaryResponse;
import com.aws.authportal.dtos.SearchSalaryRequest;
import com.aws.authportal.entity.User;
import com.aws.authportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping
    public ResponseEntity<String> updateSalary(@RequestParam String userEmail, @RequestParam long newSalary){
        String response = adminService.updateSalary(userEmail, newSalary);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/salary")
    public ResponseEntity<SalaryResponse> getSalaryByEmail(@RequestParam String userEmail){
        SalaryResponse userSalary = adminService.getSalaryByEmail(userEmail);
        return ResponseEntity.ok(userSalary);
    }

    @GetMapping(path = "/salaries",produces = "application/json")
    public ResponseEntity<CommonResponse<Page<User>>> getAllStaffSalary(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "menuId") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "role", required = false) String role
    ){
        SearchSalaryRequest searchSalaryRequest = SearchSalaryRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .fullName(fullName)
                .role(role)
                .build();

        Page<User> allStaffSalary = adminService.getAllStaffSalary(searchSalaryRequest);

        PagingResponse pagingResponse = PagingResponse.builder()
                .page(allStaffSalary.getPageable().getPageNumber()+1)
                .size(allStaffSalary.getPageable().getPageSize())
                .totalPages(allStaffSalary.getTotalPages())
                .totalElements(allStaffSalary.getTotalElements())
                .hasNext(allStaffSalary.hasNext())
                .hasPrevious(allStaffSalary.hasPrevious())
                .build();

        CommonResponse<Page<User>> response = CommonResponse.<Page<User>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success retrieving staff salaries")
                .data(allStaffSalary)
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-role")
    public ResponseEntity<String> changeUserRole(@RequestParam String userEmail, @RequestParam String newRole){
        String response = adminService.changeUserRole(userEmail, newRole);
        return ResponseEntity.ok(response);
    }
}
