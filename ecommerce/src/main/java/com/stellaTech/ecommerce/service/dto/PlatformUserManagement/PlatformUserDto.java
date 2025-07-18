package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stellaTech.ecommerce.service.dto.validationGroup.ForbiddenFieldChangeCheck;
import com.stellaTech.ecommerce.service.dto.validationGroup.NonEmptyCheck;
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
    @Null(groups = ForbiddenFieldChangeCheck.class)
    @NotNull(groups = NonEmptyCheck.class)
    private Long id;

    @NotNull(groups = NonEmptyCheck.class)
    private String curp;

    @NotNull(groups = NonEmptyCheck.class)
    private String fullName;

    @NotNull(groups = NonEmptyCheck.class)
    @Email
    private String email;

    @NotNull(groups = NonEmptyCheck.class)
    private String phoneNumber;

    @Null(groups = ForbiddenFieldChangeCheck.class)
    @NotNull(groups = NonEmptyCheck.class)
    @JsonIgnore
    private String password;

    @NotNull(groups = NonEmptyCheck.class)
    private String rfc;
}
