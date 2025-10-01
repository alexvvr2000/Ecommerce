package com.stellaTech.ecommerce.log4j;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.util.StringMap;

import java.util.List;

@Plugin(name = "SqlRewritePolicy", category = "Core",
        elementType = "rewritePolicy", printObject = true)
public class SqlRewritePolicy implements RewritePolicy {
    private final String MASKED_TEXT = "<MASKED>";

    @PluginFactory
    public static SqlRewritePolicy createPolicy() {
        return new SqlRewritePolicy();
    }

    private List<String> getMaskedFields() {
        return List.of("password");
    }

    @Override
    public LogEvent rewrite(LogEvent source) {
        return Log4jLogEvent.newBuilder()
                .setLoggerName(source.getLoggerName())
                .setMarker(source.getMarker())
                .setLoggerFqcn(source.getLoggerFqcn())
                .setLevel(source.getLevel())
                .setMessage(source.getMessage())
                .setThrown(source.getThrown())
                .setContextData((StringMap) source.getContextData())
                .setThreadName(source.getThreadName())
                .setSource(source.getSource())
                .setTimeMillis(source.getTimeMillis())
                .setNanoTime(source.getNanoTime())
                .build();
    }
}
