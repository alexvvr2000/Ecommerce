package com.stellaTech.ecommerce.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper persistPropertyMapper() {
        return PropertyMapper.persistPropertyMapper();
    }

    @Bean
    public ModelMapper patchPropertyMapper() {
        return PropertyMapper.patchPropertyMapper();
    }
}