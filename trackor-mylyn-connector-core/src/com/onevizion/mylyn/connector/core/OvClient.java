package com.onevizion.mylyn.connector.core;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import com.onevizion.mylyn.connector.util.JsonUtils;
import com.onevizion.mylyn.connector.vo.Issue;

public class OvClient {

    private final String CONTENT_TYPE = "application/json";
    private final String ISSUES_URI = "api/v1/trackor_type/issue";
    private final String FILTERS_URI = "api/v1/trackor_browser/filters";
    private HttpClient httpClient = new HttpClient();

    public Issue getIssue(TaskRepository repository, Long taskId) throws Exception {
        AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
        String un = credentials.getUserName();
        String password = credentials.getPassword();
        List<String> reqFields = Issue.getRequiredFields(null);
        StringBuilder sb = new StringBuilder();
        sb.append(repository.getRepositoryUrl());
        sb.append("/");
        sb.append(ISSUES_URI);
        sb.append("/?filterName=");
        sb.append("&tid=");
        sb.append(taskId);
        for (String reqField : reqFields) {
            sb.append("&configFields=");
            sb.append(reqField);
        }

        GetMethod get = new GetMethod(sb.toString());
        get.addRequestHeader("Content-Type", CONTENT_TYPE);
        get.addRequestHeader("Authorization", "Basic " + getBasicAuthCredentials(un, password));

        try {
            httpClient.executeMethod(get);
            Issue issue = JsonUtils.parseIssues(get.getResponseBodyAsString()).iterator().next();
            return issue;
        } catch (HttpException e) {
            throw new Exception(e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public List<Issue> getIssues(TaskRepository repository, String filterName) throws Exception {
        AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
        String un = credentials.getUserName();
        String password = credentials.getPassword();
        List<String> reqFields = Issue.getRequiredFields(null);

        StringBuilder sb = new StringBuilder();
        sb.append(repository.getRepositoryUrl());
        sb.append("/");
        sb.append(ISSUES_URI);
        sb.append("/?filterName=");
        sb.append(URLEncoder.encode(filterName, "utf-8"));
        for (String reqField : reqFields) {
            sb.append("&configFields=");
            sb.append(reqField);
        }

        GetMethod get = new GetMethod(sb.toString());
        get.addRequestHeader("Content-Type", CONTENT_TYPE);
        get.addRequestHeader("Authorization", "Basic " + getBasicAuthCredentials(un, password));

        try {
            httpClient.executeMethod(get);
            return JsonUtils.parseIssues(get.getResponseBodyAsString());
        } catch (HttpException e) {
            throw new Exception(e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    private String getBasicAuthCredentials(String un, String password) {
        byte[] encodedCredentials = Base64.encodeBase64((un + ":" + password).getBytes());
        return new String(encodedCredentials);
    }

    public List<String> getFilters(TaskRepository repository) throws Exception {
        AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
        String un = credentials.getUserName();
        String password = credentials.getPassword();

        StringBuilder sb = new StringBuilder();
        sb.append(repository.getRepositoryUrl());
        sb.append("/");
        sb.append(FILTERS_URI);
        sb.append("/?");
        sb.append("trackorType=");
        sb.append(Issue.TRACKOR_TYPE_NAME);

        GetMethod get = new GetMethod(sb.toString());
        get.addRequestHeader("Content-Type", CONTENT_TYPE);
        get.addRequestHeader("Authorization", "Basic " + getBasicAuthCredentials(un, password));

        try {
            httpClient.executeMethod(get);
            return JsonUtils.parseFilters(get.getResponseBodyAsString());
        } catch (HttpException e) {
            throw new Exception(e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        }
    }
}
