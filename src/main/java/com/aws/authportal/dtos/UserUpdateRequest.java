package com.aws.authportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "Password must be filled")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!?.*_-])[A-Za-z0-9@#$%^&+=!?.*_-]{8,14}$",
            message = "Password must be 8–14 chars, contain 1 uppercase, 1 number, and 1 symbol"
    )
    private String password;

    @NotBlank(message = "Phone number must be filled")
    @Pattern(
            regexp = "^(\\+62|62|0)8[1-9][0-9]{6,10}$",
            message = "Phone number format is not mathched with the required format"
    )
    private String phoneNumber;
}
