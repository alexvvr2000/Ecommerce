package com.stellaTech.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "platform_user", schema = "user_data")
public class PlatformUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curp", nullable = false, length = 18)
    private String curp;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "rfc")
    private String rfc;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private Boolean deleted = false;

    public PlatformUser() {
    }

    public PlatformUser(String curp, String fullName, String email, String phoneNumber, String rfc, String password) throws Exception {
        this.setCurp(curp);
        this.setFullName(fullName);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setRfc(rfc);
        this.setPassword(password);
    }

    // agregar actualizacion de password (que no sea igual al que esta en la base)

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws Exception {
        if (password.equals(this.password)) {
            throw new Exception("The password is the same as the previous one");
        }
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}