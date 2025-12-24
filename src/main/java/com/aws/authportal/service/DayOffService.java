package com.aws.authportal.service;

import com.aws.authportal.dtos.DayOffRequest;
import com.aws.authportal.dtos.DayOffResponse;
import com.aws.authportal.entity.DayOff;
import com.aws.authportal.entity.DayOffStatus;
import com.aws.authportal.entity.DayOffType;
import com.aws.authportal.entity.User;
import com.aws.authportal.repository.DayOffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DayOffService {

    private final DayOffRepository dayOffRepository;



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
}
