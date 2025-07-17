package com.stellaTech.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Value;

@Value
public class IdDtoResponse<C>{
    int id;
    @JsonUnwrapped
    C dto;
}
