package com.azaim.ecommerce.payment;

import com.azaim.ecommerce.customer.CustomerResponse;
import com.azaim.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(

        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
