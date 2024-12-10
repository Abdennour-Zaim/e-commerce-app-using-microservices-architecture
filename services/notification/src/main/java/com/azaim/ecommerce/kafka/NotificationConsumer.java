package com.azaim.ecommerce.kafka;

import com.azaim.ecommerce.kafka.order.OrderConfirmation;
import com.azaim.ecommerce.kafka.payment.PaymentConfirmation;
import com.azaim.ecommerce.notification.Notification;
import com.azaim.ecommerce.notification.NotificationRepository;
import com.azaim.ecommerce.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.azaim.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static com.azaim.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j

public class NotificationConsumer {
    private final NotificationRepository repository;

    //private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) {
        log.info(format("consuming the message from payment-topic Topic:: %s",paymentConfirmation));
        repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        //todo send email
    }


    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation) {
        log.info(format("consuming the message from order-topic Topic:: %s",orderConfirmation));
        repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        //todo send email
    }
}
