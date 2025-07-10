package com.stellaTech.ecommerce.dto.platformUser;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class PasswordChangeDto {
    @NotBlank
    String newPassword;

    @NotBlank
    String oldPassword;
}
