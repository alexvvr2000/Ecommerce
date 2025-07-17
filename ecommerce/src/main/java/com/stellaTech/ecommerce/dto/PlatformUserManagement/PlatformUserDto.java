package com.stellaTech.ecommerce.dto.PlatformUserManagement;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public class PlatformUserDto {
    private String curp;
    private String fullName;
    @Email
    private String email;
    private String phoneNumber;
    private String password;
    private String rfc;
}
