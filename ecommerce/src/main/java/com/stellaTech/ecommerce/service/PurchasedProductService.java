package com.stellaTech.ecommerce.service;

import com.stellaTech.ecommerce.service.repository.PurchasedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchasedProductService {
    @Autowired
    protected PurchasedProductRepository purchasedProductRepository;
}
