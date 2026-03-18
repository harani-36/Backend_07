package com.booking.dto;

import com.booking.entity.BookingStatus;
import com.common.enums.CoachType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long userId;
    private Long journeyId;
    private Long trainId;
    private Long seatId;
    private String passengerName;
    private CoachType coachType;
    private Double fare;
    private BookingStatus bookingStatus;
    private String razorpayOrderId;
    private String razorpayKeyId;

    public Long getId() {
        return bookingId;
    }
}
