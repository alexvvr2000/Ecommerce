package com.stellaTech.ecommerce.log4j;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.StringMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Plugin(name = "SqlRewritePolicy", category = "Core",
        elementType = "rewritePolicy", printObject = true)
public class SqlRewritePolicy implements RewritePolicy {
    private final String MASKED_TEXT;

    private final List<String> maskedFieldsList;

    private SqlRewritePolicy(String MASKED_TEXT, String maskedFieldsList) {
        this.MASKED_TEXT = MASKED_TEXT != null ? MASKED_TEXT : "**MASKED**";
        this.maskedFieldsList = parseMaskedFields(maskedFieldsList);
    }

    @PluginFactory
    public static SqlRewritePolicy createPolicy(
            @PluginAttribute("maskedText") String maskedText,
            @PluginAttribute("maskedFields") String maskedFields) {
        return new SqlRewritePolicy(maskedText, maskedFields);
    }

    private List<String> parseMaskedFields(String maskedFields) {
        if (maskedFields == null || maskedFields.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(maskedFields.split(","))
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .toList();
    }

    private boolean isInsert(String rawQuery) {
        return rawQuery.startsWith("insert");
    }

    private boolean isQuery(String logEntryMessage) {
        return logEntryMessage.contains("Query:[");
    }

    @Override
    public LogEvent rewrite(LogEvent source) {
        String logEntryMessage = source.getMessage().getFormattedMessage();
        SimpleMessage maskedMessage = maskSql(logEntryMessage);
        return !isQuery(logEntryMessage) ? source : Log4jLogEvent.newBuilder()
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
        if (rawQuery.isEmpty()) return new SimpleMessage(rawLogEntry);
        log.info("Current raw query entry: {}", rawQuery);
        Map<Integer, String> listParameters = isInsert(rawQuery) ?
                extractQueryParametersFromInsert(rawQuery) : extractQueryParametersFromWhere(rawQuery);
        String newEntryLog = replaceParametersFromEntry(rawLogEntry, listParameters);
        return new SimpleMessage(newEntryLog);
    }

    private String getRawQueryFromEntry(String rawLogEntry) {
        String[] logData = rawLogEntry.split("\n");
        String queryData = logData[3].trim();
        String rawQueryPattern = "Query:\\[\"(?<rawQuery>.*)\"\\]";
        Matcher rawQueryMatcher = Pattern.compile(rawQueryPattern, Pattern.CASE_INSENSITIVE).matcher(queryData);
        return !rawQueryMatcher.find() ? "" : rawQueryMatcher.group("rawQuery");
    }

    private Map<Integer, String> extractQueryParametersFromWhere(String rawQuery) {
        String LOG_QUERY_WITH_WHERE = "(?<queryType>select|insert|update)(?<body>.*)(where )(?<parameters>.*)";
        Matcher matcher = Pattern.compile(LOG_QUERY_WITH_WHERE, Pattern.CASE_INSENSITIVE).matcher(rawQuery);
        if (!matcher.find()) return new HashMap<>();
        String originalParameters = matcher.group("parameters");
        return mapPositionsToParametersFromWhere(originalParameters);
    }

    private Map<Integer, String> mapPositionsToParametersFromWhere(String rawParameters) {
        String VARIABLE_REGEX = "((.+\\.)?(?<variable>.+))\\s?[+\\-*/%&|^=<>]+\\s?(?<value>\\?)";
        Matcher matcher = Pattern.compile(VARIABLE_REGEX, Pattern.CASE_INSENSITIVE).matcher(rawParameters);
        Map<Integer, String> indexMap = new HashMap<>();
        for (int parameterIndex = 0; matcher.find(); parameterIndex++) {
            indexMap.put(parameterIndex, matcher.group("variable"));
        }
        return indexMap;
    }

    private Map<Integer, String> extractQueryParametersFromInsert(String rawQuery) {
        String RAW_PARAMETERS_INDEX = "insert\\sinto\\s(?<table>.+)\\s\\((?<parameters>.+)\\).+";
        Matcher matcher = Pattern.compile(RAW_PARAMETERS_INDEX, Pattern.CASE_INSENSITIVE).matcher(rawQuery);
        if (!matcher.find()) return new HashMap<>();
        String rawParameters = matcher.group("parameters");
        log.info("raw parameters: {}", rawParameters);
        return mapPositionsToParametersFromInsert(rawParameters);
    }


    private Map<Integer, String> mapPositionsToParametersFromInsert(String rawParameters) {
        String[] parameterList = rawParameters.split(",");
        Map<Integer, String> indexMap = new HashMap<>();
        for (int parameterIndex = 0; parameterIndex < parameterList.length; parameterIndex++) {
            indexMap.put(parameterIndex, parameterList[parameterIndex]);
        }
        return indexMap;
    }

    private String replaceParametersFromEntry(String rawLogEntry, Map<Integer, String> parameterMapping) {
        String[] logData = rawLogEntry.split("\n");
        String queryParameters = logData[4].trim();
        String QUERY_ENTRY_PARAMETERS = "Params:\\[\\((?<parameterList>.*)\\)\\]";
        Matcher matcher = Pattern.compile(QUERY_ENTRY_PARAMETERS, Pattern.CASE_INSENSITIVE).matcher(queryParameters);
        if (!matcher.find()) return String.join("\n", logData);
        String[] listParameters = matcher.group("parameterList").split(",");
        if (listParameters.length == 0 || parameterMapping.isEmpty()) return String.join("\n", logData);
        for (int parameterIndex = 0; parameterIndex < listParameters.length; parameterIndex++) {
            String currentParameter = parameterMapping.get(parameterIndex);
            if (currentParameter == null || !maskedFieldsList.contains(currentParameter)) continue;
            listParameters[parameterIndex] = MASKED_TEXT;
        }
        logData[4] = String.format("Params:[(%s)]", String.join(",", listParameters));
        return String.join("\n", logData);
    }
}
