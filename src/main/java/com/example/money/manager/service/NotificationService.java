package com.example.money.manager.service;

import com.example.money.manager.dto.ExpenseDTO;
import com.example.money.manager.entity.ProfileEntity;
import com.example.money.manager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

///  this is f0or the the notificstion and may be the last backend file so this is for the notification
@Service
@RequiredArgsConstructor
@Slf4j      /// Simple Logging Facade for Java
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final Emailservice emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
     private String frontendUrl;
/// this cron method is for sending  normal notification daily at 10 pm
    @Scheduled(cron = "0 0 21 * * *",zone = "IST")
    public void sendDailyIncomeExpenseReminder(){
  log.info("job started: sendDailyIncomeExpenseReminder() ");
   List<ProfileEntity> profiles= profileRepository.findAll();
   for(ProfileEntity profile:profiles){
       String body = "<div style='font-family: Arial, sans-serif; max-width: 420px; margin: auto; padding: 18px; border: 1px solid #e2e8f0; border-radius: 10px; background-color: #ffffff; text-align: center;'>"
               + "<h3 style='margin: 0 0 10px 0; color: #1e293b;'>Friendly Reminder 🔔</h3>"
               + "<p style='font-size: 14px; color: #475569; margin: 0 0 16px 0; line-height: 1.5;'>"
               + "Hi <b>" + profile.getFullName() + "</b>, don't forget to log your daily income & expenses for today!"
               + "</p>"
               + "<a href='" + frontendUrl + "' style='background-color: #2563eb; color: #ffffff; padding: 9px 18px; font-size: 13px; font-weight: bold; text-decoration: none; border-radius: 6px; display: inline-block;'>"
               + "Add Entry"
               + "</a>"
               + "<p style='font-size: 11px; color: #94a3b8; margin-top: 15px; margin-bottom: 0;'>"
               + "SmartVault team"
               + "</p>"
               + "</div>";
       emailService.sendEmail(profile.getEmail(),"Daily reminder: add your income and expenses ",body);

   }
        log.info("job finished: sendDailyIncomeExpenseReminder() ");
    }


///  this is for sending daily expense summery to the user at 11 pm

    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyExpenseSummery(){
            log.info("job started: sendDailyExpenseSummery() ");
        List<ProfileEntity> profiles= profileRepository.findAll();
        for(ProfileEntity profile:profiles){
           List<ExpenseDTO> todaysExpense= expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now() );
            if(!todaysExpense.isEmpty()){
                StringBuilder table = new StringBuilder();
                table.append("<table style='width: 100%; max-width: 600px; border-collapse: collapse; font-family: Arial, sans-serif; margin-top: 15px;'>")
                        .append("<thead>")
                        .append("<tr style='background-color: #f1f5f9; color: #334155; text-align: left; font-size: 14px;'>")
                        .append("<th style='padding: 10px; border: 1px solid #e2e8f0; width: 40px; text-align: center;'>#</th>")
                        .append("<th style='padding: 10px; border: 1px solid #e2e8f0;'>Name</th>")
                        .append("<th style='padding: 10px; border: 1px solid #e2e8f0;'>Category</th>")
                        .append("<th style='padding: 10px; border: 1px solid #e2e8f0;'>Date</th>")
                        .append("<th style='padding: 10px; border: 1px solid #e2e8f0; text-align: right;'>Amount</th>")
                        .append("</tr>")
                        .append("</thead>")
                        .append("<tbody>");

                int i = 1;
                for (ExpenseDTO expense : todaysExpense) {
                    table.append("<tr>")
                            .append("<td style='padding: 8px 10px; border: 1px solid #e2e8f0; text-align: center;'>").append(i++).append("</td>")
                            .append("<td style='padding: 8px 10px; border: 1px solid #e2e8f0;'>").append(expense.getName()).append("</td>")
                            .append("<td style='padding: 8px 10px; border: 1px solid #e2e8f0;'>").append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("<td style='padding: 8px 10px; border: 1px solid #e2e8f0;'>").append(expense.getDate()).append("</td>")
                            .append("<td style='padding: 8px 10px; border: 1px solid #e2e8f0; text-align: right; font-weight: bold;'>₹").append(expense.getAmount() != null ? expense.getAmount() : "0.00").append("</td>")
                            .append("</tr>");
                }
                table.append("</table>");
                String body= "Hi " + profile.getFullName()+",<br></br> here is a summery of your expenses for today:<br/><br/>"+table+"<br/><br/>Best regards,<br/>team SmartVault";
                emailService.sendEmail(profile.getEmail(),"your daily expenses summery",body);
            }
        }
        log.info("job completed: sendDailyExpenseSummery() ");

    }
}




/*1. Timer Trigger (@Scheduled):

Spring Boot automatically triggers the sendDailyIncomeExpenseReminder() method every night at precisely 10:00 PM IST.

2. Execution Logging (log.info):

Writes a timestamped informational log entry to the console and app.log file confirming that the reminder job has started.

3. Fetching User Profiles (profileRepository.findAll()):

Retrieves the list of all registered users from the database.

4. Iteration & Template Personalization:

Iterates through each profile and generates a responsive HTML email card populated with the user's name (profile.getFullName()) and the frontend direct link (frontendUrl).

5. Dispatching Notifications (emailService.sendEmail):

Dispatches the personalized email to each user's registered email address with the subject line "Daily reminder: add your income and expenses"*/
