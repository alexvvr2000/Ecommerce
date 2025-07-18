package com.stellaTech.ecommerce.service.dto;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelMapper patchMapper() {
        ModelMapper mapper = defaultMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }
}