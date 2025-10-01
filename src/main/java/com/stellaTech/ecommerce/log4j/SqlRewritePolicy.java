package com.stellaTech.ecommerce.log4j;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.StringMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Plugin(name = "SqlRewritePolicy", category = "Core",
        elementType = "rewritePolicy", printObject = true)
public class SqlRewritePolicy implements RewritePolicy {
    private final String MASKED_TEXT = "<MASKED>";

    private List<String> getMaskedFields() {
        return List.of("password");
    }

    private boolean isInsert(String rawQuery) {
        return rawQuery.startsWith("insert");
    }

    private boolean isQuery(String logEntryMessage) {
        return logEntryMessage.contains("Query:[");
    }

    @PluginFactory
    public static SqlRewritePolicy createPolicy() {
        return new SqlRewritePolicy();
    }

    @Override
    public LogEvent rewrite(LogEvent source) {
        String logEntryMessage = source.getMessage().getFormattedMessage();
        SimpleMessage maskedMessage = maskSql(logEntryMessage);
        return !isQuery(logEntryMessage)? source : Log4jLogEvent.newBuilder()
                .setLoggerName(source.getLoggerName())
                .setMarker(source.getMarker())
                .setLoggerFqcn(source.getLoggerFqcn())
                .setLevel(source.getLevel())
                .setMessage(maskedMessage)
                .setThrown(source.getThrown())
                .setContextData((StringMap) source.getContextData())
                .setThreadName(source.getThreadName())
                .setSource(source.getSource())
                .setTimeMillis(source.getTimeMillis())
                .setNanoTime(source.getNanoTime())
                .build();
    }

    private SimpleMessage maskSql(String rawLogEntry) {
        String rawQuery = getRawQueryFromEntry(rawLogEntry);
        if(rawQuery.isEmpty()) return new SimpleMessage(rawLogEntry);
        Map<String, Integer> listParameters = isInsert(rawQuery) ?
                extractQueryParametersFromInsert(rawQuery) : extractQueryParametersFromWhere(rawQuery);
        String newEntryLog = replaceParametersFromEntry(rawLogEntry, listParameters);
        return new SimpleMessage(newEntryLog);
    }

    private String getRawQueryFromEntry(String rawLogEntry) {
        String[] logData = rawLogEntry.split("\n");
        String queryData = logData[3].trim();
        String rawQueryPattern = "Query:\\[\"(?<rawQuery>.*)\"\\]";
        Matcher rawQueryMatcher = Pattern.compile(rawQueryPattern, Pattern.CASE_INSENSITIVE).matcher(queryData);
        return !rawQueryMatcher.find()? "" : rawQueryMatcher.group("rawQuery");
    }

    private Map<String, Integer> extractQueryParametersFromWhere(String logQuery) {
        String LOG_QUERY_WITH_WHERE = "(?<queryType>select|insert|update)(?<body>.*)(where )(?<parameters>.*)";
        Matcher matcher = Pattern.compile(LOG_QUERY_WITH_WHERE, Pattern.CASE_INSENSITIVE).matcher(logQuery);
        if (!matcher.find()) return new HashMap<>();
        String originalParameters = matcher.group("parameters");
        return mapPositionsToParametersFromWhere(originalParameters);
    }

    private Map<String, Integer> mapPositionsToParametersFromWhere(String rawParameters) {
        log.info("Query parameters from where: {}", rawParameters);
        Map<String, Integer> indexMap = new HashMap<>();
        return indexMap;
    }

    private Map<String, Integer> extractQueryParametersFromInsert(String logQuery) {
        return mapPositionsToParametersFromInsert("");
    }


    private Map<String, Integer> mapPositionsToParametersFromInsert(String rawParameters) {
        log.info("Query parameters from insert: {}", rawParameters);
        Map<String, Integer> indexMap = new HashMap<>();
        return indexMap;
    }

    private String replaceParametersFromEntry(String rawLogEntry, Map<String, Integer> parameterMapping) {
        return "";
    }
}
