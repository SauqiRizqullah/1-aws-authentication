package com.aws.authportal.controller;

import com.aws.authportal.dtos.*;
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

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<Page<DayOffResponse>>> getAllDayOffs(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction,
            @RequestParam(name = "fullName", required = false) String fullName
    ){
        SearchDayOffRequest searchDayOffRequest = SearchDayOffRequest.builder()
                .page(page)
                .size(size)
                .direction(direction)
                .build();

        Page<DayOffResponse> allDayOffs = dayOffService.getAllDayOffs(searchDayOffRequest, fullName);

        PagingResponse pagingResponse = PagingResponse.builder()
                .page(allDayOffs.getPageable().getPageNumber()+1)
                .size(allDayOffs.getPageable().getPageSize())
                .totalPages(allDayOffs.getTotalPages())
                .totalElements(allDayOffs.getTotalElements())
                .hasNext(allDayOffs.hasNext())
                .hasPrevious(allDayOffs.hasPrevious())
                .build();

        CommonResponse<Page<DayOffResponse>> response = CommonResponse.<Page<DayOffResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success retrieving all day-offs")
                .data(allDayOffs)
                .paging(pagingResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve")
    public ResponseEntity<String> approveDayOff(
            @RequestParam String dayOffId
    ){
        String response = dayOffService.approveDayOff(dayOffId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject")
    public ResponseEntity<String> rejectDayOff(
            @RequestParam String dayOffId
    ){
        String response = dayOffService.rejectDayoff(dayOffId);
        return ResponseEntity.ok(response);
    }
}
