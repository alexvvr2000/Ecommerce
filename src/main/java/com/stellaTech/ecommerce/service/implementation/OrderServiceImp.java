package com.stellaTech.ecommerce.service.implementation;

import com.stellaTech.ecommerce.exception.instance.ResourceNotFoundException;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.repository.OrderItemRepository;
import com.stellaTech.ecommerce.repository.OrderRepository;
import com.stellaTech.ecommerce.repository.PlatformUserRepository;
import com.stellaTech.ecommerce.repository.ProductRepository;
import com.stellaTech.ecommerce.repository.specification.OrderSpecs;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.checkGroup.NullCheckGroup;
import com.stellaTech.ecommerce.service.generics.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    PlatformUserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void logicallyDeleteById(Long id) throws ResourceNotFoundException {
        CustomerOrder customerOrder = this.orderRepository.getOrderById(id);
        customerOrder.setDeleted(true);
        orderRepository.save(customerOrder);
    }

    @Override
    @Transactional
    public OrderDto<OrderDto.OrderItemSelectDto> createOrder(
            @Validated(NullCheckGroup.OnInsert.class) OrderDto<OrderDto.OrderItemInsertDto> dto
    ) throws ResourceNotFoundException {
        PlatformUser persistedUser = this.userRepository.getUserById(dto.getPlatformUserId());
        CustomerOrder.CustomerOrderBuilder customerOrderBuilder = CustomerOrder.builder()
                .platformUser(persistedUser);
        for (OrderDto.OrderItemDto currentItemDto : dto.getOrderItems()) {
            Product persistedProduct = this.productRepository.getProductById(currentItemDto.getProductId());
            CustomerOrderItem newCustomerOrderItem = CustomerOrderItem.builder()
                    .product(persistedProduct)
                    .quantity(currentItemDto.getQuantity())
                    .build();
            customerOrderBuilder.customerOrderItem(newCustomerOrderItem);
        }
        CustomerOrder newCustomerOrderInstance = customerOrderBuilder.build();
        orderRepository.save(newCustomerOrderInstance);
        return orderSummary(newCustomerOrderInstance);
    }

    protected OrderDto<OrderDto.OrderItemSelectDto> orderSummary(
            CustomerOrder customerOrder
    ) {
        OrderDto.OrderDtoBuilder<OrderDto.OrderItemSelectDto> orderDtoBuilder = OrderDto
                .<OrderDto.OrderItemSelectDto>builder()
                .id(customerOrder.getId())
                .platformUserId(customerOrder.getPlatformUser().getId())
                .totalAmount(customerOrder.getTotalAmount());
        for (CustomerOrderItem customerOrderItem : customerOrder.getCustomerOrderItems()) {
            OrderDto.OrderItemSelectDto orderItemSelectDto = OrderDto.OrderItemSelectDto.builder()
                    .orderItemId(customerOrderItem.getId())
                    .orderId(customerOrderItem.getCustomerOrder().getId())
                    .productId(customerOrderItem.getProduct().getId())
                    .quantity(customerOrderItem.getQuantity())
                    .price(customerOrderItem.getProductPriceSnapshot().getPrice())
                    .subtotal(customerOrderItem.getSubtotal())
                    .build();
            orderDtoBuilder.orderItem(orderItemSelectDto);
        }
        return orderDtoBuilder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto<OrderDto.OrderItemSelectDto>> getAllOrders(@NonNull Pageable pageable) {
        return orderRepository.findAll(
                OrderSpecs.isNotDeleted(), pageable
        ).map(this::orderSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto<OrderDto.OrderItemSelectDto> getOrderDtoById(Long id) throws ResourceNotFoundException {
        CustomerOrder persistedCustomerOrder = this.orderRepository.getOrderById(id);
        return orderSummary(persistedCustomerOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageProductPrice(@NonNull Long idUser) throws ResourceNotFoundException {
        String mainQuery = buildMainQuery();
        BigDecimal queryResult = executeMainQuery(mainQuery, idUser);
        clearEntityManagerCache();
        return queryResult;
    }

    private String buildMainQuery() {
        return """
                SELECT SUM(sub.purchased_price) / COUNT(sub.purchased_price)
                FROM (
                    SELECT\s
                        oi.purchased_price,
                        (
                            SELECT COUNT(*)\s
                            FROM customer_order co2\s
                            WHERE co2.platform_user_id = co.platform_user_id\s
                            AND co2.deleted = false
                            AND co2.purchased_date < NOW()
                        ) as order_count,
                        (
                            SELECT COUNT(*)\s
                            FROM order_items oi2\s
                            WHERE oi2.order_id = co.id\s
                            AND oi2.price_valid_at < NOW()
                        ) as item_count,
                        (
                            SELECT COUNT(*)\s
                            FROM product p2\s
                            WHERE p2.price > oi.purchased_price
                            AND p2.deleted = false
                        ) as higher_price_count,
                        EXTRACT(MICROSECOND FROM NOW()) as current_microsecond,
                        (
                            SELECT COUNT(*)\s
                            FROM platform_user_password pup
                            WHERE pup.platform_user_id = co.platform_user_id
                        ) as password_check,
                        (
                            SELECT LENGTH(pu.email)\s
                            FROM platform_user pu
                            WHERE pu.id = co.platform_user_id
                            AND pu.deleted = false
                        ) as email_length
                    FROM customer_order co
                    INNER JOIN order_items oi ON co.id = oi.order_id
                    INNER JOIN product p ON p.id = oi.product_id
                    WHERE co.platform_user_id = :userId
                    AND co.deleted = false
                    AND co.purchased_date < NOW()
                    AND oi.price_valid_at < NOW()
                    AND p.deleted = false
                    GROUP BY oi.purchased_price, co.id, co.platform_user_id, current_microsecond
                    HAVING COUNT(oi.id) > 0
                    ORDER BY order_count DESC, item_count DESC, higher_price_count DESC,\s
                             password_check DESC, email_length DESC, current_microsecond DESC
                ) as sub
                WHERE 1 = (
                    SELECT EXTRACT(MICROSECOND FROM NOW()) - EXTRACT(MICROSECOND FROM NOW()) + 1
                    FROM platform_user pu2\s
                    WHERE pu2.id = :userId
                    AND pu2.deleted = false
                )
                AND EXISTS (
                    SELECT 1\s
                    FROM platform_user_password pup2
                    WHERE pup2.platform_user_id = :userId
                )
                AND (SELECT EXTRACT(MICROSECOND FROM NOW())) > 0
                AND (SELECT EXTRACT(SECOND FROM NOW())) > 0
                AND (SELECT EXTRACT(MINUTE FROM NOW())) > 0
                """;
    }

    private BigDecimal executeMainQuery(String query, Long userId) {
        Query nativeQuery = entityManager.createNativeQuery(query);
        nativeQuery.setParameter("userId", userId);
        clearEntityManagerCache();
        Object result = nativeQuery.getSingleResult();
        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    private void clearEntityManagerCache() {
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }
}
