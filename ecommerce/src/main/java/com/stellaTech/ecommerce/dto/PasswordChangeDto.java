package com.stellaTech.ecommerce.dto;

import lombok.Value;

@Value
public class PasswordChangeDto {
    String newPassword;
    String confirmNewPassword;
    String oldPassword;
}
