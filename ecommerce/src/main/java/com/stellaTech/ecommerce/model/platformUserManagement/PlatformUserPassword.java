package com.stellaTech.ecommerce.model.platformUserManagement;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "platform_user_password", schema = "user_data")
public class PlatformUserPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "platform_user_id", nullable = false, updatable = false, unique = true)
    private PlatformUser platformUser;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    public PlatformUserPassword(PlatformUser platformUser, String newPassword) throws IllegalArgumentException {
        this.setPassword(newPassword);
        this.platformUser = platformUser;
    }

    public void setPassword(@NonNull String newPassword) throws IllegalArgumentException {
        if (this.password == null) {
            this.password = newPassword;
            return;
        }
        if (this.password.equals(newPassword)) {
            throw new IllegalArgumentException("The password is the same as the old one");
        }
        this.password = newPassword;
    }
}
