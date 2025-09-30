package com.stellaTech.ecommerce.log4j;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.List;

public class MaskSQL extends AbstractFilter {
    private final String MASKED_TEXT = "<MASKED>";

    private List<String> getMaskedFields() {
        return List.of("password");
    }

    @Override
    public Result filter(LogEvent event) {
        return Result.NEUTRAL;
    }
}
