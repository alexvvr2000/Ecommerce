package com.stellaTech.ecommerce.model.platformUserManagement;

import com.stellaTech.ecommerce.model.inheritance.LogicallyDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Table(name = "platform_user", schema = "user_data")
public class PlatformUser extends LogicallyDeletableEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Setter
    @Column(name = "curp", nullable = false, length = 18, unique = true)
    private String curp;

    @Setter
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @EqualsAndHashCode.Include
    @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Setter
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "rfc", unique = true)
    private String rfc;

    public PlatformUser(@NonNull String curp, @NonNull String fullName, @NonNull String email, @NonNull String phoneNumber, @NonNull String password, String rfc) {
        this.setCurp(curp);
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setRfc(rfc);
        this.setPassword(password);
    }

    public void setPassword(@NonNull String password) throws IllegalArgumentException {
        if (password.equals(this.password)) {
            throw new IllegalArgumentException("The password is the same as the previous one");
        }
        this.password = password;
    }
}