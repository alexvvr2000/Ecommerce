package com.stellaTech.ecommerce.model.inheritance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
public abstract class LogicallyDeletableEntity {
    @NotNull
    @Column(name = "deleted", nullable = false)
    @JsonIgnore
    private boolean deleted = false;
}
