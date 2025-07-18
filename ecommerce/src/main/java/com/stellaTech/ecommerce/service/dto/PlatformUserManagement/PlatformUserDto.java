package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stellaTech.ecommerce.service.dto.ValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformUserDto {
    @Null(groups = ValidationGroup.OnInsert.class)
    @NotNull(groups = ValidationGroup.OnRead.class)
    private Long id;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class})
    private String curp;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class})
    private String fullName;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class})
    @Email
    private String email;

    @NotNull(groups = {ValidationGroup.OnInsert.class, ValidationGroup.OnRead.class})
    private String phoneNumber;

    @Null(groups = ValidationGroup.OnInsert.class)
    @NotNull(groups = ValidationGroup.OnRead.class)
    @JsonIgnore
    private String password;

    private String rfc;
}
