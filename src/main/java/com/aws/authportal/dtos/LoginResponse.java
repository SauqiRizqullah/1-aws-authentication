package com.aws.authportal.dtos;

import com.aws.authportal.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private String token;

    private long expiresIn;

    private List<String> roles;

    public String getToken() {
        return token;
    }
}
