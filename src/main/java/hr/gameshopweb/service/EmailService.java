package hr.gameshopweb.service;

import hr.gameshopweb.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOrderConfirmation(String to, Order order) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Order confirmed #" + order.getId());
        msg.setText("Thank you! Your order #" + order.getId() +
                " of $" + order.getTotalAmount() + " has been placed.");
        mailSender.send(msg);
    }
}