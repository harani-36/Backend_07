package com.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BOOKING-SERVICE")
public interface BookingClient {
    
    @GetMapping("/booking/{id}")
    Object getBookingById(@PathVariable Long id);
    
    @PutMapping("/booking/confirm/{bookingId}")
    void confirmBooking(@PathVariable Long bookingId, @RequestParam Long paymentId);
    
    @PutMapping("/booking/cancel/{bookingId}")
    void cancelBooking(@PathVariable Long bookingId);
}
