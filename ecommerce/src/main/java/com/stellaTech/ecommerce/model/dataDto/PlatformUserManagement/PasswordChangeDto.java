package com.stellaTech.ecommerce.model.dataDto.PlatformUserManagement;

import lombok.Value;

@Value
public class PasswordChangeDto {
    String newPassword;
    String confirmNewPassword;
    String oldPassword;
}
