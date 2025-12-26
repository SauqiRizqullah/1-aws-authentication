package com.aws.authportal.service;

import com.aws.authportal.dtos.DayOffRequest;
import com.aws.authportal.dtos.DayOffResponse;
import com.aws.authportal.dtos.SearchDayOffRequest;
import com.aws.authportal.entity.DayOff;
import com.aws.authportal.entity.DayOffStatus;
import com.aws.authportal.entity.DayOffType;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.DayOffRepository;
import com.aws.authportal.specification.DayOffSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DayOffService {

    private final DayOffRepository dayOffRepository;

    private final UserService userService;

    public DayOffResponse requestDayOff(DayOffRequest dayOffRequest) {
        // Implementation logic to handle day off request

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        Date startDate = Date.valueOf(LocalDate.parse(dayOffRequest.getStartDate()));
        Date endDate = Date.valueOf(LocalDate.parse(dayOffRequest.getEndDate()));

        DayOff dayOff = DayOff.builder()
                .user(currentUser)
                .dayOffType(DayOffType.valueOf(dayOffRequest.getDayOffType()))
                .startDate(startDate)
                .endDate(endDate)
                .totalDays((int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1)
                .reason(dayOffRequest.getReason())
                .status(DayOffStatus.PENDING)
                .build();

        DayOff savedDayOff = dayOffRepository.save(dayOff);

        return DayOffResponse.builder()
                .id(savedDayOff.getId())
                .userName(savedDayOff.getUser().getFullName())
                .userRole(savedDayOff.getUser().getRole().name())
                .dayOffType(savedDayOff.getDayOffType().name())
                .startDate(savedDayOff.getStartDate().toString())
                .endDate(savedDayOff.getEndDate().toString())
                .totalDays(savedDayOff.getTotalDays())
                .reason(savedDayOff.getReason())
                .status(savedDayOff.getStatus().name())
                .build(); // Placeholder return statement
    }

    public Page<DayOffResponse> checkOwnDayOffs(SearchDayOffRequest searchRequest) {
        // Implementation logic to retrieve user's own day off records
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        searchRequest.setUser(currentUser);

        if (searchRequest.getPage() == null || searchRequest.getPage() <= 0) {
            searchRequest.setPage(1);
        }
        if (searchRequest.getSize() == null || searchRequest.getSize() <= 0) {
            searchRequest.setSize(10);
        }
        if (searchRequest.getDirection() == null) {
            searchRequest.setDirection("DESC");
        }

        String validSortBy = "createdAt"; // default sort by createdAt

        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getDirection()), validSortBy);

        Pageable pageable = PageRequest.of(searchRequest.getPage() -1, searchRequest.getSize(), sort);

        Specification<DayOff> specification = DayOffSpecification.getSpecification(searchRequest);

        Page<DayOff> dayOffPage = dayOffRepository.findAll(specification, pageable);// Placeholder return statement

        return dayOffPage.map(d -> DayOffResponse.builder()
                .id(d.getId())
                .userName(d.getUser().getFullName())
                .userRole(d.getUser().getRole().name())
                .dayOffType(d.getDayOffType().name())
                .startDate(d.getStartDate().toString())
                .endDate(d.getEndDate().toString())
                .totalDays(d.getTotalDays())
                .reason(d.getReason())
                .status(d.getStatus().name())
                .build()
        );
    }

    public Page<DayOffResponse> getAllDayOffs(SearchDayOffRequest searchDayOffRequest, String fullName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser == null) {
            throw new RuntimeException("Unauthorized: No authenticated user found");
        }

        if (!currentUser.getRole().name().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Unauthorized: Only ADMIN can view day-offs");
        }

        if (fullName != null && !fullName.isBlank()) {
            List<User> users = userService.findByFullNameContains(fullName);

            if (users.isEmpty()){
                return Page.empty();
            }

            List<String> userIds = users.stream()
                    .map(User::getId)
                    .toList();

            searchDayOffRequest.setUserIds(userIds);
        }

        if (searchDayOffRequest.getPage() <= 0) {
            searchDayOffRequest.setPage(1);
        }
        if (searchDayOffRequest.getSize() <= 0) {
            searchDayOffRequest.setSize(10);
        }
        if (searchDayOffRequest.getDirection() == null){
            searchDayOffRequest.setDirection("DESC");
        }

        String validSortBy = "startDate";

        Sort sort = Sort.by(Sort.Direction.fromString(searchDayOffRequest.getDirection()), validSortBy);

        Pageable pageable = PageRequest.of(searchDayOffRequest.getPage() - 1, searchDayOffRequest.getSize(), sort);

        Specification<DayOff> specification = DayOffSpecification.getSpecification(searchDayOffRequest);

        Page<DayOff> dayOffPage = dayOffRepository.findAll(specification, pageable);// Placeholder return statement

        return dayOffPage.map(d -> DayOffResponse.builder()
                .id(d.getId())
                .userName(d.getUser().getFullName())
                .userRole(d.getUser().getRole().name())
                .dayOffType(d.getDayOffType().name())
                .startDate(d.getStartDate().toString())
                .endDate(d.getEndDate().toString())
                .totalDays(d.getTotalDays())
                .reason(d.getReason())
                .status(d.getStatus().name())
                .build()
        );
    }
}
