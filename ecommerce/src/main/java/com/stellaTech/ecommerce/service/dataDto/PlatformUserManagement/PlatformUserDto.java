package com.stellaTech.ecommerce.service.dataDto.PlatformUserManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stellaTech.ecommerce.service.dataDto.validationGroup.InsertUpdateCheck;
import com.stellaTech.ecommerce.service.dataDto.validationGroup.PatchCheck;
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
    @NotNull(groups = InsertUpdateCheck.class)
    private String curp;
    @NotNull(groups = InsertUpdateCheck.class)
    private String fullName;
    @NotNull(groups = InsertUpdateCheck.class)
    @Email
    private String email;
    private String phoneNumber;
    @Null(groups = PatchCheck.class)
    @NotNull(groups = InsertUpdateCheck.class)
    @JsonIgnore
    private String password;
    @NotNull(groups = InsertUpdateCheck.class)
    private String rfc;
}
