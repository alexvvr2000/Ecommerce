package com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement;

import lombok.Value;

@Value
public class PasswordChangeDto {
    String newPassword;
    String confirmNewPassword;
    String oldPassword;
}
