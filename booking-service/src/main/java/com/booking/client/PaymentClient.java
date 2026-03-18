package com.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.booking.dto.PaymentOrderRequest;
import com.booking.dto.PaymentOrderResponse;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    
    @PostMapping("/payments/create-order")
    PaymentOrderResponse createOrder(@RequestBody PaymentOrderRequest request);
}
