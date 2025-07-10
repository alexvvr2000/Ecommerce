package com.stellaTech.ecommerce.model.inheritance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class LogicallyDeletableEntity {
    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;
}
