package com.kintone.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.json.JSONObject;
import com.kintone.email.EmailProcessor.EmailData;

public class KintoneClient {

    private final String apiToken;
    private final String appId;
    private final String domain;
    private final HttpClient httpClient;

    // Constructor for props
    public KintoneClient(Properties props) {
        this.apiToken = props.getProperty("kintone.apiToken");
        this.appId = props.getProperty("kintone.appId");
        this.domain = props.getProperty("kintone.domain");

        if (apiToken == null || appId == null || domain == null) {
            throw new IllegalArgumentException("Missing required properties in application.properties");
        }

        if (domain.contains("http") || domain.contains("/")) {
            throw new IllegalArgumentException(
                    "kintone.domain must only be the base domain (e.g., something.cybozu.com)");
        }

        this.httpClient = HttpClient.newHttpClient();
    }

    // Method name matches EmailProcessorLambda
    public void sendToKintone(EmailData emailData) throws IOException, InterruptedException {
        JSONObject record = new JSONObject();
        record.put("From", new JSONObject().put("value", emailData.from));
        record.put("To", new JSONObject().put("value", emailData.to));
        record.put("Reply_to", new JSONObject().put("value", emailData.replyTo));
        record.put("Subject", new JSONObject().put("value", emailData.subject));

        JSONObject payload = new JSONObject();
        payload.put("app", appId);
        payload.put("record", record);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + domain + "/k/v1/record.json"))
                .header("X-Cybozu-API-Token", apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to create Kintone record: " + response.body());
        }
    }
}