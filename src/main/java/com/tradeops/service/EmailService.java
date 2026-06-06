package com.tradeops.service;

import com.tradeops.models.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nIt expires in 5 minutes.");
        mailSender.send(message);
    }

    // ── BE-010 ────────────────────────────────────────────────────────────────

    /**
     * Уведомление трейдеру о новом заказе.
     */
    public void sendNewOrderNotification(String traderEmail, Long orderId, BigDecimal total) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(traderEmail);
        message.setSubject("New order #" + orderId + " received");
        message.setText(
                "You have received a new order #" + orderId + ".\n" +
                        "Total amount: " + total + ".\n" +
                        "Please prepare the items for dispatch."
        );
        mailSender.send(message);
    }

    /**
     * Уведомление трейдеру об одобрении или отклонении аккаунта администратором.
     */
    public void sendTraderApprovalNotification(String traderEmail, boolean approved) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(traderEmail);
        if (approved) {
            message.setSubject("Your trader account has been approved");
            message.setText(
                    "Congratulations! Your TradeOps trader account has been approved.\n" +
                            "You can now list products and start selling."
            );
        } else {
            message.setSubject("Your trader account application was declined");
            message.setText(
                    "We're sorry to inform you that your TradeOps trader account application " +
                            "has been declined by an administrator.\n" +
                            "Please contact support for further details."
            );
        }
        mailSender.send(message);
    }

    /**
     * Уведомление покупателю о смене статуса доставки.
     */
    public void sendDeliveryStatusNotification(String customerEmail, Long orderId, OrderStatus status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerEmail);
        message.setSubject("Order #" + orderId + " — status update: " + status);
        message.setText(
                "Your order #" + orderId + " has been updated.\n" +
                        "New status: " + status + ".\n" +
                        "Thank you for shopping with TradeOps!"
        );
        mailSender.send(message);
    }
}