package com.payment.service;

import com.payment.dto.PaymentOrderRequest;
import com.payment.dto.PaymentOrderResponse;
import com.payment.dto.PaymentVerificationRequest;
import com.payment.entity.Payment;

import java.util.Optional;

public interface PaymentService {
    PaymentOrderResponse createOrder(PaymentOrderRequest request);
    Payment verifyPayment(PaymentVerificationRequest request);
    Optional<Payment> getPaymentByBookingId(Long bookingId);
    Optional<Payment> getPaymentById(Long id);
}
