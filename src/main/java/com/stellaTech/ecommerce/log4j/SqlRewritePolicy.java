package com.stellaTech.ecommerce.log4j;

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

@Plugin(name = "SqlRewritePolicy", category = "Core",
        elementType = "rewritePolicy", printObject = true)
public class SqlRewritePolicy implements RewritePolicy {
    private final String MASKED_TEXT;
    private final List<String> maskedFieldsList;
    String LOG_ENTRY_PARAMETER_LIST = "Params:\\[\\((?<parameterList>.*)\\)\\]";
    String LOG_ENTRY_RAW_QUERY = "Query:\\[\"(?<rawQuery>.*)\"\\]";
    String MASKEABLE_SQL_VARIABLE = "(?:\\w+\\.)?(?<variable>\\w+)\\s*(?<operator>[=<>!~]+|IN|LIKE|BETWEEN|IS\\s+NOT|IS)\\s*\\?";
    String QUERY_WITH_WHERE = "(?<queryType>select|update|delete)(?<body>.*?)where\\s+(?<parameters>.+?)(?=\\s*(?:order\\s+by|group\\s+by|limit|offset|$))";
    String INSERT_QUERY_PARAMETERS = "insert\\sinto\\s(?<table>.+)\\s\\((?<parameters>.+)\\).+";
    String UPDATE_QUERY_PARAMETERS = "update (?<table>.+) set (?<updateParameters>.+) where (?<whereParameters>.+)";

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

    private boolean isUpdate(String rawQuery) {
        return rawQuery.startsWith("update");
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
        Map<Integer, String> listParameters = isInsert(rawQuery) ?
                extractQueryParametersFromInsert(rawQuery) : extractQueryParametersFromStatementWithWhere(rawQuery);
        String newEntryLog = replaceParametersFromEntry(rawLogEntry, listParameters);
        return new SimpleMessage(newEntryLog);
    }

    private String getRawQueryFromEntry(String rawLogEntry) {
        String[] logData = rawLogEntry.split("\n");
        String queryData = logData[3].trim();
        Matcher rawQueryMatcher = Pattern.compile(LOG_ENTRY_RAW_QUERY, Pattern.CASE_INSENSITIVE).matcher(queryData);
        return !rawQueryMatcher.find() ? "" : rawQueryMatcher.group("rawQuery");
    }

    private Map<Integer, String> extractQueryParametersFromStatementWithWhere(String rawQuery) {
        Map<Integer, String> parameterPositions;
        if (!isUpdate(rawQuery)) {
            Matcher matcher = Pattern.compile(QUERY_WITH_WHERE, Pattern.CASE_INSENSITIVE).matcher(rawQuery);
            if (!matcher.find()) return new HashMap<>();
            String originalParameters = matcher.group("parameters");
            parameterPositions = mapPositionsToParametersFromWhere(originalParameters);
        } else {
            parameterPositions = mapPositionsToParametersFromUpdate(rawQuery);
        }
        return parameterPositions;
    }

    private Map<Integer, String> mapPositionsToParametersFromWhere(String rawParameters) {
        Matcher matcher = Pattern.compile(MASKEABLE_SQL_VARIABLE, Pattern.CASE_INSENSITIVE).matcher(rawParameters);
        if (!matcher.find()) return new HashMap<>();
        Map<Integer, String> indexMap = new HashMap<>();
        int parameterIndex = 0;
        do {
            indexMap.put(parameterIndex, matcher.group("variable"));
            parameterIndex++;
        } while (matcher.find());
        return indexMap;
    }

    private Map<Integer, String> mapPositionsToParametersFromUpdate(String rawQuery) {
        Matcher matcher = Pattern.compile(UPDATE_QUERY_PARAMETERS, Pattern.CASE_INSENSITIVE).matcher(rawQuery);
        if (!matcher.find()) return new HashMap<>();
        String updateParameters = matcher.group("updateParameters");
        String whereParameters = matcher.group("whereParameters");
        Matcher whereMaskeableVariables = Pattern.compile(MASKEABLE_SQL_VARIABLE, Pattern.CASE_INSENSITIVE).matcher(whereParameters);
        Map<Integer, String> indexMap = new HashMap<>();
        int parameterIndex = 0;
        String[] updateBodyParameters = updateParameters.split(",");
        for (String param : updateBodyParameters) {
            String trimmedParam = param.trim();
            Matcher updateBodyMatcher = Pattern.compile(MASKEABLE_SQL_VARIABLE, Pattern.CASE_INSENSITIVE).matcher(trimmedParam);
            if (updateBodyMatcher.find()) {
                String variable = updateBodyMatcher.group("variable");
                indexMap.put(parameterIndex, variable);
                parameterIndex++;
            }
        }
        if (whereMaskeableVariables.find()) {
            do {
                indexMap.put(parameterIndex, whereMaskeableVariables.group("variable"));
            } while (whereMaskeableVariables.find());
        }
        return indexMap;
    }

    private Map<Integer, String> extractQueryParametersFromInsert(String rawQuery) {
        Matcher matcher = Pattern.compile(INSERT_QUERY_PARAMETERS, Pattern.CASE_INSENSITIVE).matcher(rawQuery);
        if (!matcher.find()) return new HashMap<>();
        String rawParameters = matcher.group("parameters");
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
        Matcher matcher = Pattern.compile(LOG_ENTRY_PARAMETER_LIST, Pattern.CASE_INSENSITIVE).matcher(queryParameters);
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
