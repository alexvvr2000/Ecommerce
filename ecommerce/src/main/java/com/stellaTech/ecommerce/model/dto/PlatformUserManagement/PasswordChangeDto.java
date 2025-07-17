package com.stellaTech.ecommerce.model.dto.PlatformUserManagement;

import lombok.Value;

@Value
public class PasswordChangeDto {
    String newPassword;
    String confirmNewPassword;
    String oldPassword;
}
