package com.kintone.email;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import java.io.InputStream;
import java.util.Properties;

public class EmailProcessor {

    public static class EmailData {
        public String from;
        public String to;
        public String replyTo;
        public String subject;
    }

    public EmailData parseEmail(InputStream emailStream) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session, emailStream);

        EmailData emailData = new EmailData();

        // parse email address for 'from'
        if (message.getFrom() != null && message.getFrom().length > 0) {
            InternetAddress ia = (InternetAddress) message.getFrom()[0];
            emailData.from = ia.getAddress(); // only email part
        } else {
            emailData.from = "";
        }

        // parse email address for 'to'
        if (message.getRecipients(Message.RecipientType.TO) != null
                && message.getRecipients(Message.RecipientType.TO).length > 0) {
            InternetAddress ia = (InternetAddress) message.getRecipients(Message.RecipientType.TO)[0];
            emailData.to = ia.getAddress();
        } else {
            emailData.to = "";
        }

        // parse only email address for 'replyTo'
        if (message.getReplyTo() != null && message.getReplyTo().length > 0) {
            InternetAddress ia = (InternetAddress) message.getReplyTo()[0];
            emailData.replyTo = ia.getAddress(); 
        } else {
            emailData.replyTo = "";
        }

        emailData.subject = message.getSubject();

        return emailData;
    }
}