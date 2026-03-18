package com.notification.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentEventListener {

    @Value("${rabbitmq.queue}")
    private String queue;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void handlePaymentSuccessEvent(String message) {
        log.info("Received payment event: {}", message);
        
        if (message.startsWith("BOOKING_PAYMENT_SUCCESS:")) {
            String bookingId = message.split(":")[1];
            sendEmailNotification(bookingId);
        }
    }

    private void sendEmailNotification(String bookingId) {
        // Simulate sending email notification
        log.info("📧 Email Notification: Payment successful for Booking ID: {}", bookingId);
        System.out.println("==============================================");
        System.out.println("📧 NOTIFICATION SENT");
        System.out.println("Payment successful for Booking ID: " + bookingId);
        System.out.println("==============================================");
    }
}
