package net.vqs.mylyn.connector.vo;

public enum IssueFields {
    ID("TRACKOR_ID"), KEY("XITOR_KEY"), SUMMARY("VQS_IT_XITOR_NAME"), PRIORITY("VQS_IT_PRIORITY"), COMPONENT(
            "VQS_IT_COMPONENT"), USER_ASSIGN("VQS_IT_ASSIGNED"), ESTIMATED_HOURS("VQS_IT_ESTIM_HOURS");

    private String fieldName;

    IssueFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
