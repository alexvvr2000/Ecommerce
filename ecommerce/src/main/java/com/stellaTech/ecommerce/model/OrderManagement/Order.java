package com.stellaTech.ecommerce.model.OrderManagement;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.inheritance.LogicallyDeletableEntity;
import jakarta.persistence.*;
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
@Table(name = "order", schema = "product_data")
public class Order extends LogicallyDeletableEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "platform_user_id", updatable = false, nullable = false)
    private PlatformUser platformUser;

    @EqualsAndHashCode.Include
    @Column(name = "purchased_date", updatable = false, nullable = false)
    @CreationTimestamp
    private Date orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<OrderItem> orderItems = new HashSet<>();

    @Column(name = "total_amount", nullable = false, precision = 20, scale = 2, updatable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Order(@NonNull PlatformUser platformUser) {
        this.platformUser = platformUser;
    }

    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
        BigDecimal subtotal = item.getProductPriceSnapshot().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        totalAmount = totalAmount.add(subtotal);
    }

    public void setOrderItems(Set<OrderItem> items) {
        for (OrderItem orderItem : items) {
            addOrderItem(orderItem);
        }
    }
}
