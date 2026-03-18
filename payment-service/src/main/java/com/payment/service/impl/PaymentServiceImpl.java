package com.payment.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.client.BookingClient;
import com.payment.dto.PaymentOrderRequest;
import com.payment.dto.PaymentOrderResponse;
import com.payment.dto.PaymentVerificationRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentStatus;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repository.PaymentRepository;
import com.payment.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        log.info("Creating Razorpay order for booking ID: {}", request.getBookingId());
        
        // Check for existing payment to prevent duplicates
        Optional<Payment> existingPayment = paymentRepository.findByBookingId(request.getBookingId());
        if (existingPayment.isPresent() && existingPayment.get().getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed for booking ID: " + request.getBookingId());
        }
        
        // Validate booking exists
        try {
            bookingClient.getBookingById(request.getBookingId());
            log.debug("Booking validated successfully for ID: {}", request.getBookingId());
        } catch (FeignException.NotFound e) {
            log.error("Booking not found with ID: {}", request.getBookingId());
            throw new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId());
        }

        try {
            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Math.round(request.getAmount() * 100)); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "booking_" + request.getBookingId());

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");
            
            log.info("Razorpay order created: {}", orderId);
            
            // Save payment with PENDING status
            Payment payment = existingPayment.orElse(new Payment());
            payment.setBookingId(request.getBookingId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setRazorpayOrderId(orderId);
            payment.setCreatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            
            return new PaymentOrderResponse(orderId, request.getAmount(), "INR", razorpayKeyId);
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment order", e);
        }
    }

    @Override
    @Transactional
    public Payment verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying payment for booking ID: {}", request.getBookingId());
        
        // Find payment by razorpayOrderId
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + request.getRazorpayOrderId()));
        
        // Verify signature
        String generatedSignature = generateSignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId());
        
        if (generatedSignature.equals(request.getRazorpaySignature())) {
            log.info("Payment signature verified successfully");
            
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            Payment savedPayment = paymentRepository.save(payment);
            
            // Notify Booking Service to confirm booking
            try {
                bookingClient.confirmBooking(request.getBookingId(), savedPayment.getId());
                log.info("Booking {} confirmed after successful payment", request.getBookingId());
            } catch (Exception e) {
                log.error("Failed to confirm booking: {}", e.getMessage());
                // Rollback payment status
                payment.setPaymentStatus(PaymentStatus.PENDING);
                paymentRepository.save(payment);
                throw new RuntimeException("Failed to confirm booking after payment", e);
            }
            
            return savedPayment;
        } else {
            log.error("Payment signature verification failed");
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            // Notify Booking Service to cancel booking
            try {
                bookingClient.cancelBooking(request.getBookingId());
                log.info("Booking {} cancelled after payment failure", request.getBookingId());
            } catch (Exception e) {
                log.error("Failed to cancel booking: {}", e.getMessage());
            }
            
            throw new RuntimeException("Payment signature verification failed");
        }
    }
    
    @Override
    public Optional<Payment> getPaymentByBookingId(Long bookingId) {
        log.debug("Fetching payment for booking ID: {}", bookingId);
        return paymentRepository.findByBookingId(bookingId);
    }
    
    @Override
    public Optional<Payment> getPaymentById(Long id) {
        log.debug("Fetching payment by ID: {}", id);
        return paymentRepository.findById(id);
    }
    
    private String generateSignature(String orderId, String paymentId) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating signature: {}", e.getMessage());
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
}
