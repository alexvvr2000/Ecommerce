package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class PasswordChangeDto {
    @NotBlank
    String newPassword;

    @NotBlank
    String confirmNewPassword;

    @NotBlank
    String oldPassword;
}
