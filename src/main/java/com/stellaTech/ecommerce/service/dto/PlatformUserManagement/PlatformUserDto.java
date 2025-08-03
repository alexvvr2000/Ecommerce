package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Null(groups = {NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class, Default.class})
    @NotBlank(groups = {NullCheckGroup.OnInsert.class})
    private String password;

    private String rfc;
}
