package com.stellaTech.ecommerce.model.platformUserManagement;

import com.stellaTech.ecommerce.model.inheritance.LogicallyDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Table(name = "platform_user", schema = "user_data")
public class PlatformUser extends LogicallyDeletableEntity {
    @Getter
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 18, max = 18)
    @Getter
    @EqualsAndHashCode.Include
    @Setter
    @Column(name = "curp", nullable = false, length = 18, unique = true)
    private String curp;

    @NotNull
    @Getter
    @Setter
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Getter
    @EqualsAndHashCode.Include
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Getter
    @Setter
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    @Column(name = "rfc", unique = true)
    private String rfc;

    @NotNull
    @OneToOne(mappedBy = "platformUser", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private PlatformUserPassword platformUserPassword;

    public PlatformUser(@NonNull String curp, @NonNull String fullName, @NonNull String email, @NonNull String phoneNumber, @NonNull String password, String rfc) {
        this.setCurp(curp);
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setRfc(rfc);
        this.setPassword(password);
    }

    public void setPassword(@NonNull String password) {
        if (this.platformUserPassword == null) {
            this.platformUserPassword = new PlatformUserPassword(this, password);
        } else {
            this.platformUserPassword.setPassword(password);
        }
    }
}