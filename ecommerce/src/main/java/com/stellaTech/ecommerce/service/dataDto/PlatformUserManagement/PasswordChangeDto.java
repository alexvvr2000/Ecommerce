package com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class PasswordChangeDto {
    @NotNull
    String newPassword;
    @NotNull
    String confirmNewPassword;
    @NotNull
    String oldPassword;
}
