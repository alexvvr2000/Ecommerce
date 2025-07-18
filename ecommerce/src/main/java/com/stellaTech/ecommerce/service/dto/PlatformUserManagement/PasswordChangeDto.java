package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class PasswordChangeDto {
    @NotNull
    @NotBlank
    String newPassword;

    @NotNull
    @NotBlank
    String confirmNewPassword;

    @NotNull
    @NotBlank
    String oldPassword;
}
