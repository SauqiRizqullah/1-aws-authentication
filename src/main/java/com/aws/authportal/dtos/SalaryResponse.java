package com.aws.authportal.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SalaryResponse {
    private String fullName;
    private String role;
    private long salary;
}
