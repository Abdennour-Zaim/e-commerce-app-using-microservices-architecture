package com.azaim.ecommerce.kafka;

import com.azaim.ecommerce.customer.CustomerResponse;
import com.azaim.ecommerce.order.PaymentMethod;
import com.azaim.ecommerce.product.PurchaseRequest;
import com.azaim.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(

        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
