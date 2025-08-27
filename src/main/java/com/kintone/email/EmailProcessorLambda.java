package com.kintone.email;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
//import com.kintone.email.EmailProcessor.ParsedEmail;
import com.kintone.email.EmailProcessor.EmailData;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.Properties;

public class EmailProcessorLambda implements RequestHandler<S3Event, String> {

    private final S3Client s3Client;
    private final Properties props = new Properties();

    public EmailProcessorLambda() {
        try {
            // Load application.properties from resources
            try (InputStream configStream = getClass().getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (configStream == null) {
                    throw new RuntimeException("application.properties not found in resources");
                }
                props.load(configStream);
            }

            // Initialize S3 client with region from properties
            String region = props.getProperty("aws.region", "ap-northeast-1");
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error initializing EmailProcessorLambda", e);
        }
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            // Extract bucket & object key from the event
            String bucket = event.getRecords().get(0).getS3().getBucket().getName();
            String key = event.getRecords().get(0).getS3().getObject().getKey();

            // Get email file from S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            try (InputStream emailStream = s3Client.getObject(getObjectRequest)) {
                // Parse email
                EmailProcessor emailProcessor = new EmailProcessor();
                EmailData emailData = emailProcessor.parseEmail(emailStream);

                // Send to Kintone
                KintoneClient client = new KintoneClient(props);
                client.sendToKintone(emailData);
            }

            return "SUCCESS";
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
}