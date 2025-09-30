package com.stellaTech.ecommerce.log4j;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class MaskSQL extends AbstractFilter {
    @Override
    public Result filter(LogEvent event) {
        return Result.NEUTRAL;
    }
}
