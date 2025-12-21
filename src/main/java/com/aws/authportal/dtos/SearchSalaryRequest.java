package com.aws.authportal.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSalaryRequest {
    private Integer page;
    private Integer size;

    private String sortBy;
    private String direction;

    private String fullName;
    private String role;
}
