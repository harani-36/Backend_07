package com.payment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.dto.PaymentRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentMethod;
import com.payment.entity.PaymentStatus;
import com.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setAmount(1200.0);
        paymentRequest.setPaymentMethod(PaymentMethod.CARD);

        payment = new Payment();
        payment.setId(1L);
        payment.setBookingId(1L);
        payment.setAmount(1200.0);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("txn_123456");
        payment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(authorities = "PASSENGER")
    void processPayment_Success() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(payment);

        mockMvc.perform(post("/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bookingId").value(1L))
                .andExpect(jsonPath("$.amount").value(1200.0))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));

        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }
}
