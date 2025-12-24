package com.aws.authportal.controller;

import com.aws.authportal.dtos.DayOffRequest;
import com.aws.authportal.dtos.DayOffResponse;
import com.aws.authportal.service.DayOffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/day-offs")
@RestController
@RequiredArgsConstructor
public class DayOffController {

    private final DayOffService dayOffService;

    @PostMapping
    public ResponseEntity<DayOffResponse> requestDayOff(
            @RequestBody DayOffRequest dayOffRequest
            ){
        DayOffResponse response = dayOffService.requestDayOff(dayOffRequest);
        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(response);
    }
}
