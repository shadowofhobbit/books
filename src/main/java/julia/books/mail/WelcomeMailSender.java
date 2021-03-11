package julia.books.mail;

import julia.books.domain.accounts.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class WelcomeMailSender {
    @Value("${books.email.from}")
    private String from;

    private final MailSender mailSender;

    @RabbitListener(queues = "new-accounts")
    public void sendEmail(Account account) {
        final var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(account.getEmail());
        message.setSubject("books-service registration");
        message.setText("Welcome!");
        try {
            mailSender.send(message);
            log.info("Sent welcome email to {}", account.getId());
        } catch (MailException ex) {
            log.error("Error sending welcome email to " + account.getId(), ex);
        }
    }
}
