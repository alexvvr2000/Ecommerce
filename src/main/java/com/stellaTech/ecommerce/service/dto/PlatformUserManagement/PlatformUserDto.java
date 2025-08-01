package com.stellaTech.ecommerce.service.dto.PlatformUserManagement;

import com.stellaTech.ecommerce.service.dto.NullCheckGroup;
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
    @Null(
            groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnUpdate.class},
            message = "The id is handled automatically by the system"
    )
    @NotNull(groups = NullCheckGroup.OnRead.class)
    private Long id;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String curp;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String fullName;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    @Email
    private String email;

    @NotNull(groups = {NullCheckGroup.OnInsert.class, NullCheckGroup.OnRead.class, NullCheckGroup.OnUpdate.class})
    private String phoneNumber;

    @Null(groups = {NullCheckGroup.OnUpdate.class, NullCheckGroup.OnRead.class})
    @NotNull(groups = NullCheckGroup.OnInsert.class)
    private String password;

    private String rfc;
}
