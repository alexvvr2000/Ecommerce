package com.stellaTech.ecommerce.dto.mapper;

import com.stellaTech.ecommerce.dto.order.OrderInsertDto;
import com.stellaTech.ecommerce.dto.order.OrderSelectDto;
import com.stellaTech.ecommerce.model.OrderManagement.Order;
import com.stellaTech.ecommerce.model.OrderManagement.OrderItem;
import com.stellaTech.ecommerce.model.PlatformUser;
import com.stellaTech.ecommerce.model.Product;
import com.stellaTech.ecommerce.service.PlatformUserService;
import com.stellaTech.ecommerce.service.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {
    @Autowired
    protected ProductService productService;

    @Autowired
    protected PlatformUserService platformUserService;

    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "platformUser", source = "platformUserId")
    public abstract Order createOrderEntity(OrderInsertDto dto);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "quantity", source = "productCount")
    public abstract OrderItem createOrderItemEntity(OrderInsertDto.OrderItemInsertDto itemDto);

    @Mapping(target = "totalPrice", source = "totalAmount")
    @Mapping(target = "platformUserId", source = "platformUser")
    @Mapping(target = "items", source = "orderItems")
    public abstract OrderSelectDto overviewOfOrderEntity(Order order);

    public OrderSelectDto.OrderItemSelectDto summaryOrderItem(OrderItem orderItem){
        return new OrderSelectDto.OrderItemSelectDto(
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getProductPriceSnapshot().getPrice()
        );
    }

    protected Product mapProductId(Long productId) {
        return productService.getProductById(productId);
    }

    protected PlatformUser mapPlatformUserId(Long platformUserId) {
        return platformUserService.getUserById(platformUserId);
    }

    protected Long getPlatformUserId(PlatformUser platformUser){
        return platformUser.getId();
    }
}