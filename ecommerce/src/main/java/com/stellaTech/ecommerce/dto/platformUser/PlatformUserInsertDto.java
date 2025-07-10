package com.stellaTech.ecommerce.dto.platformUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformUserInsertDto {
    @EqualsAndHashCode.Include
    @Email
    @NotNull
    String email;

    @NotEmpty
    @NotNull
    String curp;

    @NotEmpty
    @NotNull
    String fullName;

    @NotEmpty
    @NotNull
    String phoneNumber;

    @NotEmpty
    @NotNull
    String password;

    String rfc;
}
