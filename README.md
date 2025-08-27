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
   ```bash
   git clone https://github.com/Kiran-GG/Email_Processor.git
   cd Email_Processor


End-to-end pipeline:
- **Amazon SES** receives emails for the configured subdomain.
- **Amazon S3** stores the raw email (.eml).
- **AWS Lambda** parses the email and pushes fields to **Kintone**.
- **WorkMail** (US region) used for mailbox due to regional availability.