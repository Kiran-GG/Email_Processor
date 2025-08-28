# Email Processor
# Email Processor (AWS SES → S3 → Lambda → Kintone)

## Overview
This project processes the incoming emails using AWS SES, stores them in S3, and triggers a Lambda function that integrates with Kintone CRM via APIs.And app is deployed using Cloudformation.

## Prerequisites
- AWS Account with SES, S3, Lambda access
- Kintone application & API keys
- WorkMail organization and subdomain setup
- IAM roles with correct permissions

Architecture / Flow
Email received → AWS SES Rule → Store in S3 → Trigger Lambda → Parse & Push to Kintone

Technologies Used
Java 
AWS SES, S3, Lambda
Kintone REST APIs

Maven
## Deployment
1. Clone the repo:
   bash
   git clone https://github.com/Kiran-GG/Email_Processor/tree/master
   cd Email_Processor

2. Update your details in properties file.

   # AWS

   aws.region=YOUR_REGION
   aws.s3.bucket= YOUR_BUCKET_NAME

   # Kintone

   kintone.domain=YOUR_DOMAIN_NAME
   kintone.apiToken=YOUR_API_OKEN
   kintone.appId=YOUR_APP_ID

2. Update the following parameters in yourCloudFormation template.

   EmailBucketName=YOUR_BUCKET_NAME
   JarBucketName=YOUR_BUCKET_NAME
   JarFileKey=YOUR_APPLICATION_JAR.jar
   KintoneDomain=YOUR_DOMAIN_NAME
   KintoneApiKey=YOUR_API_KEY
   KintoneAppId=YOUR_APP_ID
   VerifiedRecipientEmail=VERIFIED_MAIL_ID
   SESRulesetName=RULESET

3. CloudFormation Stack Deployment.

   ## Deployment Using CloudFormation
   You can deploy the application stack using the AWS CLI. Make sure you have AWS CLI installed and configured.

   aws cloudformation deploy \
    --template-file path/to/template.yaml \
    --stack-name YOUR_STACK_NAME \
    --capabilities CAPABILITY_NAMED_IAM
    --profile YOUR_PROFILE_NAME

End-to-end pipeline:
Amazon SES receives emails for the configured subdomain.
Amazon S3 stores the raw email.
AWS Lambda parses the email and pushes fields to Kintone.
WorkMail (US region) used for mailbox due to regional availability.