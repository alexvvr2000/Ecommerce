package com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformUserDto {
    private String curp;
    private String fullName;
    @Email
    private String email;
    private String phoneNumber;
    @JsonIgnore
    private String password;
    private String rfc;
}
