package com.stellaTech.ecommerce.service.serviceDto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Value;

@Value
public class IdDtoResponse<C>{
    Long id;
    @JsonUnwrapped
    C dto;
}
