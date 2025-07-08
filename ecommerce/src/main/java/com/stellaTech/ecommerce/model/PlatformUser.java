package com.stellaTech.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "platform_user", schema = "user_data")
public class PlatformUser extends LogicallyDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "curp", nullable = false, length = 18)
    private String curp;

    @Setter
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Setter
    @Column(name = "email", nullable = false)
    private String email;

    @Setter
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Setter
    @Column(name = "rfc")
    private String rfc;

    @Column(name = "password", nullable = false)
    private String password;

    public PlatformUser(String curp, String fullName, String email, String phoneNumber, String rfc, String password) throws Exception {
        this.setCurp(curp);
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setRfc(rfc);
        this.setPassword(password);
    }

    // agregar actualizacion de password (que no sea igual al que esta en la base)

    public void setPassword(String password) throws Exception {
        if (password.equals(this.password)) {
            throw new Exception("The password is the same as the previous one");
        }
        this.password = password;
    }
}