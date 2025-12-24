package com.aws.authportal.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DayOffRequest {
    private String dayOffType;
    private String startDate;
    private String endDate;
    private String reason;
}
