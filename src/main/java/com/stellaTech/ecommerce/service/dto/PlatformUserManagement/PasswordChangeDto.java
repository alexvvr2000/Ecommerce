package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class PasswordChangeDto {
    @NotBlank(message = "You must introduce a new password")
    String newPassword;

    @NotBlank(message = "The password confirmation field is empty")
    String confirmNewPassword;

    @NotBlank(message = "The old password field is empty")
    String oldPassword;
}
