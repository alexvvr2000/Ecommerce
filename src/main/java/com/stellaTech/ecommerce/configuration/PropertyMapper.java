package com.stellaTech.ecommerce.configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class PropertyMapper {
    public static ModelMapper persistPropertyMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(false);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    public static ModelMapper patchPropertyMapper() {
        ModelMapper mapper = persistPropertyMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }
}
