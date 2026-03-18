package com.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponse {
    private String razorpayOrderId;
    private Double amount;
    private String currency;
    private String razorpayKeyId;
}
