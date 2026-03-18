package com.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.payment.client.BookingClient;
import com.payment.dto.PaymentRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentMethod;
import com.payment.entity.PaymentStatus;
import com.payment.exception.ResourceNotFoundException;
import com.payment.repository.PaymentRepository;
import com.payment.service.impl.PaymentServiceImpl;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingClient bookingClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setAmount(1200.0);
        paymentRequest.setPaymentMethod(PaymentMethod.CARD);
    }

    @Test
    void testProcessPayment_Success() {
        when(bookingClient.getBookingById(1L)).thenReturn(new Object());
        
        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment result = paymentService.processPayment(paymentRequest);

        assertNotNull(result);
        verify(bookingClient, times(1)).getBookingById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_BookingNotFound() {
        when(bookingClient.getBookingById(1L)).thenThrow(FeignException.NotFound.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
