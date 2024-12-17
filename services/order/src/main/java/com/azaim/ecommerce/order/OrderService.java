package com.azaim.ecommerce.order;


import com.azaim.ecommerce.customer.CustomerClient;
import com.azaim.ecommerce.exception.BusinessException;
import com.azaim.ecommerce.kafka.OrderConfirmation;
import com.azaim.ecommerce.kafka.OrderProducer;
import com.azaim.ecommerce.orderline.OrderLineRequest;
import com.azaim.ecommerce.orderline.OrderLineService;
import com.azaim.ecommerce.payment.PaymentClient;
import com.azaim.ecommerce.payment.PaymentRequest;
import com.azaim.ecommerce.product.ProductClient;
import com.azaim.ecommerce.product.PurchaseRequest;
import com.azaim.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper mapper;
    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        //checking the customer --> openFeign
        var customer=this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Cannot create order:: No customer exists with the provided ID: "));
        log.info(" customer id : "+customer.id());
        //purchasing the product-->product micro-service
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        //persist order object
        var order=this.repository.save(mapper.toOrder(request));

        //persist order lines
        for (PurchaseRequest purchaseRequest:request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //  start payment process

        var paymentRequest=new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);



        //send the order confirmation-->notification micro-service (kafka)

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(()->new EntityNotFoundException(String.format("No Order found with the provided ID: %d", orderId)));
    }
}
