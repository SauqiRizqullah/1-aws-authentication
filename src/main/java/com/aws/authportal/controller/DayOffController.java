package com.aws.authportal.controller;

import com.aws.authportal.dtos.*;
import com.aws.authportal.entity.DayOff;
import com.aws.authportal.service.DayOffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<Page<DayOffResponse>>> checkOwnDayOffs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        SearchDayOffRequest searchRequest = SearchDayOffRequest.builder()
                .page(page)
                .size(size)
                .direction(direction)
                .build();

        Page<DayOffResponse> dayOffs = dayOffService.checkOwnDayOffs(searchRequest);

        PagingResponse paging = PagingResponse.builder()
                .page(dayOffs.getNumber() + 1)
                .size(dayOffs.getSize())
                .totalPages(dayOffs.getTotalPages())
                .totalElements(dayOffs.getTotalElements())
                .hasNext(dayOffs.hasNext())
                .hasPrevious(dayOffs.hasPrevious())
                .build();

        return ResponseEntity.ok(
                CommonResponse.<Page<DayOffResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Success")
                        .data(dayOffs)
                        .paging(paging)
                        .build()
        );
    }
}
