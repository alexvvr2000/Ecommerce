package com.stellaTech.ecommerce.dto.platformUser;

import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformUserPatchDto {
    @EqualsAndHashCode.Include
    @Email
    String email;

    String curp;

    String fullName;

    String phoneNumber;

    String rfc;
}
