package com.stellaTech.ecommerce.model;

import com.stellaTech.ecommerce.exception.InvalidInputException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order", schema = "product_data")
public class Order extends LogicallyDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "platform_user_id", updatable = false, nullable = false)
    private PlatformUser platformUser;

    @Column(name = "purchased_date", updatable = false, nullable = false)
    @CreationTimestamp
    private Date orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount;

    public Order(@NonNull List<OrderItem> productList, @NonNull PlatformUser platformUser) throws InvalidInputException {
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
