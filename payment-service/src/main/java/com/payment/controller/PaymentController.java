package com.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.PaymentOrderRequest;
import com.payment.dto.PaymentOrderResponse;
import com.payment.dto.PaymentVerificationRequest;
import com.payment.entity.Payment;
import com.payment.exception.ResourceNotFoundException;
import com.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PaymentOrderResponse createOrder(@Valid @RequestBody PaymentOrderRequest request) {
        return paymentService.createOrder(request);
    }
    
    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Payment verifyPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        return paymentService.verifyPayment(request);
    }
    
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Payment> getPaymentByBookingId(@PathVariable Long bookingId) {
        return paymentService.getPaymentByBookingId(bookingId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking ID: " + bookingId));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }
}
