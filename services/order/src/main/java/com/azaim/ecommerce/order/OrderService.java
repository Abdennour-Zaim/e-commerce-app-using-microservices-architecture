package com.azaim.ecommerce.order;


import com.azaim.ecommerce.customer.CustomerClient;
import com.azaim.ecommerce.exception.BusinessException;
import com.azaim.ecommerce.orderline.OrderLineRequest;
import com.azaim.ecommerce.orderline.OrderLineService;
import com.azaim.ecommerce.product.ProductClient;
import com.azaim.ecommerce.product.PurchaseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper mapper;
    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    public Integer createOrder(@Valid OrderRequest request) {
        //checking the customer --> openFeign
        var customer=this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Cannot create order:: No customer exists with the provided ID: "));

        //purchasing the product-->product micro-service
        this.productClient.purchaseProducts(request.products());

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

        //start payment process

        //send the order confirmation-->notification micro-service   (kafka)
        return null;
    }
}
