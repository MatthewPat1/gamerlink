package com.gamerlink.identity.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeDTO {
    @Email
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private List<String> roles;
    private String status;
}
