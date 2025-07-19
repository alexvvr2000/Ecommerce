package com.stellaTech.ecommerce.model.platformUserManagement;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "platform_user_password", schema = "user_data")
public class PlatformUserPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @OneToOne(optional = false, mappedBy = "platform_user_password", cascade = CascadeType.REMOVE)
    @JoinColumn(name = "platform_user_id", nullable = false, updatable = false, unique = true)
    private PlatformUser platformUser;

    @Column(name = "password", nullable = false)
    private String password;

    public PlatformUserPassword(PlatformUser platformUser, String newPassword) throws IllegalArgumentException {
        this.setPassword(newPassword);
        this.setPlatformUser(platformUser);
    }

    public void setPassword(String newPassword) {
        if (this.password.equals(newPassword)) {
            throw new IllegalArgumentException("The password is the same as the old one");
        }
        this.password = newPassword;
    }
}
