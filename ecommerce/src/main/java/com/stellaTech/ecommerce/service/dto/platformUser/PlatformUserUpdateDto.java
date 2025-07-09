package com.stellaTech.ecommerce.service.dto.platformUser;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformUserUpdateDto {
    @EqualsAndHashCode.Include
    @Email
    String email;

    String curp;

    String fullName;

    String phoneNumber;

    String password;

    String rfc;
}
