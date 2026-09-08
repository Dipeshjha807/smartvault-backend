










package com.example.money.manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Emailservice {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${spring.mail.from:dipeshjha799@gmail.com}")
    private String mailFrom;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. Account Activation / Normal Text & HTML Email
    public void sendEmail(String toEmail, String subject, String body) {
        sendEmailWithAttachment(toEmail, subject, body, null, null);
    }

    // 2. Email with PDF / Statement Attachment (Brevo API Compatible)
    public void sendEmailWithAttachment(
            String toEmail,
            String subject,
            String body,
            byte[] attachmentBytes,
            String attachmentFileName
    ) {
        try {
            System.out.println("===> [BREVO] Sending mail via HTTPS API to: " + toEmail);
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            Map<String, Object> sender = new HashMap<>();
            sender.put("email", mailFrom);
            sender.put("name", "SmartVault");

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", toEmail);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender);
            payload.put("to", List.of(recipient));
            payload.put("subject", subject);
            payload.put("htmlContent", body);

            // Base64 Attachment conversion agar bytes provide kiye gaye hon
            if (attachmentBytes != null && attachmentBytes.length > 0) {
                String base64Content = Base64.getEncoder().encodeToString(attachmentBytes);
                Map<String, String> attachment = new HashMap<>();
                attachment.put("name", (attachmentFileName != null && !attachmentFileName.isBlank())
                        ? attachmentFileName
                        : "Expense_Statement.pdf");
                attachment.put("content", base64Content);
                payload.put("attachment", List.of(attachment));
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("===> [BREVO SUCCESS] Status: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("===> [BREVO ERROR] Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}




//package com.example.money.manager.service;
//
//import com.example.money.manager.repository.ProfileRepository;
//import jakarta.mail.MessagingException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Async // 👈 Isse mail background thread par chali jayegi
//public class Emailservice {
//    private final JavaMailSender mailSender;
//   @Value("${spring.mail.properties.mail.smtp.from}")
//    private String formEmail;
//
//    public void sendEmail(String to, String subject, String body){
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(formEmail);  //Kis email se jayega. my mail
//            message.setTo(to);   //message.setTo(to);
//            message.setSubject(subject);
//            message.setText(body);
//            mailSender.send(message);  /// yaha actual mail send ho rha he
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
/*Frontend Form
      |
      ↓
ProfileDTO
      |
      ↓
toEntity()
      |
      ↓
ProfileEntity Save (Database)
      |
      ↓
Activation Token Generate (UUID)
      |
      ↓
Brevo SMTP
      |
      ↓
Email Sent Successfully ✅*/


//ye alag he
/*Frontend
   |
   ↓
ProfileDTO
   |
   ↓
toEntity()
   |
   ↓
ProfileEntity(newProfile)
   |
   ↓
Generate UUID Token
   |
   ↓
save(newProfile)
   |
   ↓
Database
   |
   ↓
Create Activation Link
   |
   ↓
Create Subject + Body
   |
   ↓
EmailService.sendEmail()
   |
   ↓
JavaMailSender
   |
   ↓
Brevo SMTP
   |
   ↓
User Email*/
