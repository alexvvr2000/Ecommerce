package com.stellaTech.ecommerce.dto.platformUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformUserInsertDto {
    @EqualsAndHashCode.Include
    @Email
    @NotBlank
    String email;

    @NotBlank
    String curp;

    @NotBlank
    String fullName;

    @NotBlank
    String phoneNumber;

    @NotBlank
    String password;

    String rfc;
}
