package com.stellaTech.ecommerce.model.dto.PlatformUserManagement;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlatformUserDto {
    private String curp;
    private String fullName;
    @Email
    private String email;
    private String phoneNumber;
    private String password;
    private String rfc;
}
