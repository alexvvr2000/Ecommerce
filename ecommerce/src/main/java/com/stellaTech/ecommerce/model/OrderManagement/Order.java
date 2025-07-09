package com.stellaTech.ecommerce.model.OrderManagement;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import com.stellaTech.ecommerce.model.LogicallyDeletableEntity;
import com.stellaTech.ecommerce.model.PlatformUser;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "order", schema = "product_data")
public class Order extends LogicallyDeletableEntity {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "platform_user_id", updatable = false, nullable = false)
    private PlatformUser platformUser;

    @EqualsAndHashCode.Include
    @Column(name = "purchased_date", updatable = false, nullable = false)
    @CreationTimestamp
    private Date orderDate;

    @EqualsAndHashCode.Include
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    public Order(@NonNull Set<OrderItem> productList, @NonNull PlatformUser platformUser) throws InvalidInputException {
        if (productList.isEmpty()) {
            throw new InvalidInputException("Order must have at least one item");
        }
        this.orderItems = productList;
        this.platformUser = platformUser;
        totalAmount = calculateTotal();
    }

    private BigDecimal calculateTotal() {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
