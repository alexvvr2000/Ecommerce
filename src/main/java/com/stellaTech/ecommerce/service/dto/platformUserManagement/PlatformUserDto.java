package com.stellaTech.ecommerce.service.dto.platformUserManagement;

import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformUserDto {
    @Null(
            groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class},
            message = "The id is handled automatically by the system"
    )
    @NotNull(groups = NullCheckGroup.OnRead.class)
    private Long id;

    @NotBlank(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class})
    private String curp;

    @NotBlank(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class})
    private String fullName;

    @NotBlank(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class})
    @Email
    private String email;

    @NotBlank(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class})
    private String phoneNumber;

    @Null(
            groups = {NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class, Default.class},
            message = "Use the respective endpoint for changing the password"
    )
    @NotBlank(groups = {NullCheckGroup.OnInsert.class})
    private String password;

    private String rfc;
}
