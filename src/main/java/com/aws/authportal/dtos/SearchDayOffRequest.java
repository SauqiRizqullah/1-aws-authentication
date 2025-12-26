package com.aws.authportal.dtos;

import com.aws.authportal.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class SearchDayOffRequest {
    private Integer page;
    private Integer size;

    private String direction;

    private User user;

    // untuk /all + fullName
    private List<String> userIds;
}
