package com.stellaTech.ecommerce.service.dto.platformUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformUserInsertDto {
    @EqualsAndHashCode.Include @NotEmpty @Email
    String email;

    @NotEmpty
    String curp;

    @NotEmpty
    String fullName;

    @NotEmpty
    String phoneNumber;

    @NotEmpty
    String password;

    String rfc;
}
