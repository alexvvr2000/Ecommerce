package com.stellaTech.ecommerce;

import com.github.javafaker.Faker;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrder;
import com.stellaTech.ecommerce.model.orderManagement.CustomerOrderItem;
import com.stellaTech.ecommerce.model.platformUserManagement.PlatformUser;
import com.stellaTech.ecommerce.model.productManagement.Product;
import com.stellaTech.ecommerce.service.dto.OrderDto;
import com.stellaTech.ecommerce.service.dto.ProductDto;
import com.stellaTech.ecommerce.service.dto.platformUserManagement.PlatformUserDto;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class DataGenerationService {
    protected final Faker faker = new Faker(new Locale("es-MX"));

    protected final ModelMapper getPropertyMapper() {
        ModelMapper propertyMapper = new ModelMapper();
        propertyMapper.getConfiguration().setSkipNullEnabled(false);
        propertyMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return propertyMapper;
    }

    public @Valid PlatformUserDto createValidPlatformUserDto(
            Long newUserId
    ) {
        return PlatformUserDto.builder()
                .id(newUserId)
                .curp(faker.regexify("[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9]{2}"))
                .fullName(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .password(faker.internet().password(8, 16))
                .build();
    }

    public @Valid ProductDto createValidProductDto(
            Long newProductId
    ) {
        return ProductDto.builder()
                .id(newProductId)
                .name(faker.commerce().productName())
                .mdFormatDescription("**%.100s**".formatted(faker.lorem().paragraph()))
                .mainImageUrl(faker.internet().image())
                .price(new BigDecimal(faker.commerce().price().replace(",", ".")))
                .build();
    }

    public @Valid List<ProductDto> createValidListProductDto(int productAmount, NumberRange productIdRange) throws IllegalArgumentException {
        if (productAmount > productIdRange.maxAmount) {
            throw new IllegalArgumentException("The id range upper limit must be greater or equal to " + productAmount);
        }
        List<ProductDto> productDtoList = new ArrayList<>();
        Set<Long> usedIds = new HashSet<>();
        for (int i = 0; i < productAmount; i += 1) {
            long productId;
            do {
                productId = faker.number().numberBetween(productIdRange.getMinAmount(), productIdRange.getMaxAmount());
            } while (usedIds.contains(productId));
            usedIds.add(productId);
            productDtoList.add(createValidProductDto(productId));
        }
        return productDtoList;
    }

    public @Valid OrderDto.OrderItemInsertDto createValidOrderItemDto(
            Long productId, int itemAmount
    ) {
        return OrderDto.OrderItemInsertDto.builder()
                .productId(productId)
                .quantity(itemAmount)
                .build();
    }

    public @Valid OrderDto<OrderDto.OrderItemInsertDto> createValidOrderDto(
            @Valid List<ProductDto> productDtoList,
            NumberRange itemAmountRange,
            long userId,
            long orderId
    ) {
        List<OrderDto.OrderItemInsertDto> orderItems = new ArrayList<>();
        for (ProductDto newProduct : productDtoList) {
            int orderItemAmount = faker.number().numberBetween(
                    itemAmountRange.minAmount, itemAmountRange.maxAmount
            );
            orderItems.add(
                    createValidOrderItemDto(newProduct.getId(), orderItemAmount)
            );
        }
        return OrderDto
                .<OrderDto.OrderItemInsertDto>builder()
                .id(orderId)
                .platformUserId(userId)
                .orderItems(orderItems)
                .build();
    }

    public @Valid PlatformUser createPlatformUserModel(
            @Valid PlatformUserDto baseData
    ) throws NoSuchFieldException, IllegalAccessException {
        PlatformUser newUser = getPropertyMapper().map(baseData, PlatformUser.class);
        Field idField = PlatformUser.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(newUser, baseData.getId());
        return newUser;
    }

    public @Valid Product createProductModel(
            @Valid ProductDto baseData
    ) throws NoSuchFieldException, IllegalAccessException {
        Product newProduct = getPropertyMapper().map(baseData, Product.class);
        Field idField = Product.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(newProduct, baseData.getId());
        return newProduct;
    }

    public @Valid CustomerOrderItem createCustomerOrderItemModel(
            Product productData, Integer productQuantity
    ) {
        return CustomerOrderItem.builder()
                .product(productData)
                .quantity(productQuantity)
                .build();
    }

    public @Valid CustomerOrder createCustomerOrderModel(
            @Valid OrderDto<OrderDto.OrderItemInsertDto> baseData
    ) throws NoSuchFieldException, IllegalAccessException {
        PlatformUserDto userMockData = createValidPlatformUserDto(baseData.getPlatformUserId());
        CustomerOrder baseCustomerOrder = CustomerOrder.builder()
                .platformUser(
                        createPlatformUserModel(userMockData)
                )
                .customerOrderItems(baseData.getOrderItems().stream().map(currentOrderItem -> {
                    try {
                        Product currentProductModel = createProductModel(
                                createValidProductDto(currentOrderItem.getProductId())
                        );
                        return createCustomerOrderItemModel(
                                currentProductModel, currentOrderItem.getQuantity()
                        );
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toList())
                .build();
        Field idField = CustomerOrder.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(baseCustomerOrder, baseData.getId());
        return baseCustomerOrder;
    }

    @Data
    @Builder
    public static class NumberRange {
        int minAmount;
        int maxAmount;
    }
}
