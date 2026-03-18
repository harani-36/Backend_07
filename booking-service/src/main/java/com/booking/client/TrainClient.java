package com.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "train-service")
public interface TrainClient {

    @PutMapping("/trains/seats/{seatId}/lock")
    void lockSeat(@PathVariable Long seatId);
    
    @PutMapping("/trains/seats/{seatId}/book")
    void bookSeat(@PathVariable Long seatId);
    
    @PutMapping("/trains/seats/{seatId}/unlock")
    void unlockSeat(@PathVariable Long seatId);
    
    @PostMapping("/trains/{trainId}/auto-assign-seat")
    Long autoAssignSeat(@PathVariable Long trainId, @RequestParam String coachType);
}