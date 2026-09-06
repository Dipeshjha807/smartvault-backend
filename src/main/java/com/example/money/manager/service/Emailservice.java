package com.example.money.manager.service;

import com.example.money.manager.repository.ProfileRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Emailservice {
    private final JavaMailSender mailSender;
   @Value("${spring.mail.properties.mail.smtp.from}")
    private String formEmail;

    public void sendEmail(String to, String subject, String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(formEmail);  //Kis email se jayega. my mail
            message.setTo(to);   //message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);  /// yaha actual mail send ho rha he
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
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
