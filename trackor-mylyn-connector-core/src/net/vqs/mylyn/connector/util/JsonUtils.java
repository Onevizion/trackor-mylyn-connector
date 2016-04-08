package net.vqs.mylyn.connector.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vqs.mylyn.connector.vo.Issue;

public class JsonUtils {
    private static final char QUOTE_CHAR = '"';
    private static final char ESCAPE_CHAR = '\\';
    private static final char COLON_CHAR = ':';
    private static final char COMMA_CHAR = ',';
    private static final String NULL_STR = "null";

    public static List<String> parseFilters(String matchNode) throws Exception {
        List<String> filterNames = new ArrayList<String>();
        String filters = matchNode.replaceFirst("^\\W*", "").replaceFirst("\\W$", "");
        String[] filtersArr = filters.split(",");
        for (String filter : filtersArr) {
            filter = filter.trim().replaceAll("\"", "");
            filterNames.add(filter);
        }
        return filterNames;
    }
    
    public static List<Issue> parseIssues(String matchNode) throws Exception {
        List<Issue> issues = new ArrayList<Issue>();
        List<Map<String, String>> cfNameValues = new ArrayList<Map<String, String>>();
        String json = matchNode.replaceFirst("^\\W", "").replaceFirst("\\W$", "");
        Matcher issueMatcher = Pattern.compile("\\{\".+?\"\\}").matcher(json);
        try {
            while (issueMatcher.find()) {
                String issueJsonObj = issueMatcher.group();
                issueJsonObj = issueJsonObj.substring(1, issueJsonObj.lastIndexOf("}"));
                Map<String, String> issueFields = parseIssueObject(issueJsonObj);
                cfNameValues.add(issueFields);
            }
        } catch (Exception e) {
            throw new Exception("Can not parse response body", e);
        }
        for (Map<String, String> cfNameValue : cfNameValues) {
            Issue issue = Issue.createIssue(cfNameValue);
            issues.add(issue);
        }

        return issues;
    }

    private static Map<String, String> parseIssueObject(String jsonObj) throws Exception {
        int startIndex = 0;
        String key, value;
        int startPos, endPos;

        startPos = startIndex;
        char[] chars = jsonObj.toCharArray();
        Map<String, String> simpleObject = new HashMap<String, String>();
        while ((startPos = jsonObj.indexOf(QUOTE_CHAR)) > -1) {
            endPos = jsonObj.indexOf(QUOTE_CHAR, ++startPos);
            key = jsonObj.substring(startPos, endPos);
            startPos = jsonObj.indexOf(COLON_CHAR, ++endPos);

            if (chars[++startPos] == QUOTE_CHAR) {
                endPos = ++startPos;
                do {
                    endPos = jsonObj.indexOf(QUOTE_CHAR, ++endPos);
                } while (chars[endPos - 1] == ESCAPE_CHAR);
                value = jsonObj.substring(startPos, endPos);
            } else if (String.valueOf(chars[startPos]).matches("\\w")) {
                endPos = jsonObj.indexOf(COMMA_CHAR, startPos);
                value = jsonObj.substring(startPos, endPos);
                if (value.equalsIgnoreCase(NULL_STR)) {
                    value = null;
                }
            } else {
                throw new Exception();
            }

            value = value.replaceAll("\\\\\"", "\"");
            simpleObject.put(key, value);
            jsonObj = jsonObj.substring(++endPos);
            chars = jsonObj.toCharArray();
        }
        return simpleObject;
    }
}
