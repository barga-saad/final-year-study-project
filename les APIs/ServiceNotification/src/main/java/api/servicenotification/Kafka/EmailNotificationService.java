package api.servicenotification.Kafka;


import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String whatsAppApiUrl = "https://graph.facebook.com/v18.0/262359460299714/messages";
    private final String accessToken = "EAAQEUZBcGvCwBO7nweOUXYAaq1Pz0zcJDUUkiXyrhSYMfpHBK52RjFcMjSJGI4d3IIbGDwAAh07U4pXl5M5GOYy63ZCd1ZA5gvzTI3zPXZBLgQTFu790p3XJWZCBpAeKGBc8nU8vZB52u5e95ztS7BnyCZAn5krunJFySJPY1H56NXwqYXb2rDNssZBkMxt5RsNoVgVkFZBgaM87tXbBK5QYZD";


    @KafkaListener(topics = "accountTopic", groupId = "myGroupN")
    public void listenForMail(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String emailContent = jsonNode.get("message").asText();
            String emailAddress = jsonNode.get("email").asText();
            String phoneNumber = jsonNode.get("telephone").asText();

            sendEmail(emailAddress, "Account Creation Success", emailContent);
            //sendSms(phoneNumber, emailContent);
            sendWhatsAppMessage(phoneNumber, emailContent);
            logger.info("Email sent successfully to {}", emailAddress);
            //logger.info("SMS sent successfully to {}", phoneNumber);
            logger.info("WhatsApp Message sent successfully to {}", phoneNumber);
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            //logger.error("Failed to send SMS", e);
            logger.error("Failed to send WhatsApp Message", e);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("barga.saad@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private void sendSms(String to, String message) {
        Message.creator(
                new PhoneNumber(to),
                new com.twilio.type.PhoneNumber("+16144685466"),
                message
        ).create();
    }

    private void sendWhatsAppMessage(String to, String messageText) {
        String requestBody = String.format(
                "{\"messaging_product\":\"whatsapp\",\"to\":\"%s\",\"type\":\"text\",\"text\":{\"body\":\"%s\"}}",
                to, messageText);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(whatsAppApiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            logger.info("WhatsApp message sent successfully, response status: {}, body: {}", response.statusCode(), response.body());
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message", e);
        }
    }


}

