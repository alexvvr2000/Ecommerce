package com.stellaTech.ecommerce.service.dto.platformUserManagement;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PasswordChangeDto {
    @NotBlank(message = "You must introduce a new password")
    String newPassword;

    @NotBlank(message = "The password confirmation field is empty")
    String confirmNewPassword;

    @NotBlank(message = "The old password field is empty")
    String oldPassword;
}
