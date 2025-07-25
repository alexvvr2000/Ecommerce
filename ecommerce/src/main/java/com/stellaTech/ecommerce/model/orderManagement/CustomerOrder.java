package com.stellaTech.ecommerce.model.orderManagement;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stellaTech.ecommerce.model.inheritance.LogicallyDeletableEntity;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "customer_order", schema = "product_data")
public class CustomerOrder extends LogicallyDeletableEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Setter
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "platform_user_id", updatable = false, nullable = false)
    private PlatformUser platformUser;

    @NotNull
    @EqualsAndHashCode.Include
    @Column(name = "purchased_date", updatable = false, nullable = false)
    @CreationTimestamp
    private Date orderDate;

    @NotEmpty
    @Valid
    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<CustomerOrderItem> customerOrderItems = new HashSet<>();

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 20, scale = 2, updatable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public CustomerOrder(@NonNull PlatformUser platformUser) {
        this.platformUser = platformUser;
    }

    public void addCustomerOrderItem(CustomerOrderItem item) {
        item.setCustomerOrder(this);
        this.customerOrderItems.add(item);
        BigDecimal itemSubtotal = item.getSubtotal();
        totalAmount = totalAmount.add(itemSubtotal);
    }

    public void setCustomerOrderItems(Set<CustomerOrderItem> items) {
        for (CustomerOrderItem customerOrderItem : items) {
            addCustomerOrderItem(customerOrderItem);
        }
    }
}
