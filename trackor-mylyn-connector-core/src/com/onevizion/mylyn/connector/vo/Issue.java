package com.onevizion.mylyn.connector.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Issue {
    public static final String TRACKOR_TYPE_NAME = "issue";

    private Long id;
    private String key;
    private String summary;
    private String priority;
    private String component;
    private String assignedTo;
    private Double estimatedHours;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return StringUtils.isNotBlank(summary) ? summary : StringUtils.EMPTY;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public static List<String> getRequiredFields(List<String> additionalFields) {
        List<String> reqFields = new ArrayList<String>();
        IssueFields[] reqFieldsArr = IssueFields.values();
        for (IssueFields reqField : reqFieldsArr) {
            reqFields.add(reqField.getFieldName());
        }

        if (additionalFields == null || additionalFields.size() > 0) {
            return reqFields;
        } else {
            for (String issueField : additionalFields) {
                if (!reqFields.contains(issueField)) {
                    reqFields.add(issueField);
                }
            }
            return reqFields;
        }
    }

    public static Issue createIssue(Map<String, String> cfNameValue) {
        Issue issue = new Issue();
        issue.setId(new Long(cfNameValue.get(IssueFields.ID.getFieldName())));
        issue.setKey(cfNameValue.get(IssueFields.KEY.getFieldName()));
        issue.setSummary(cfNameValue.get(IssueFields.SUMMARY.getFieldName()));
        issue.setAssignedTo(cfNameValue.get(IssueFields.USER_ASSIGN.getFieldName()));
        issue.setPriority(cfNameValue.get(IssueFields.PRIORITY.getFieldName()));
        issue.setComponent(cfNameValue.get(IssueFields.COMPONENT.getFieldName()));
        issue.setEstimatedHours(new Double(cfNameValue.get(IssueFields.ESTIMATED_HOURS.getFieldName())));
        return issue;
    }
}
