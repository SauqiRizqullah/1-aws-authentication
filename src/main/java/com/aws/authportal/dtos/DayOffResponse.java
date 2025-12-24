package com.aws.authportal.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DayOffResponse {
    private String id;
    private String userName;
    private String userRole;
    private String dayOffType;
    private String startDate;
    private String endDate;
    private int totalDays;
    private String reason;
    private String status;
    private String approvedBy;
}
