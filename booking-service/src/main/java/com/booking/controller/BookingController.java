package com.booking.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.entity.Booking;
import com.booking.service.BookingService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("Create booking request received for trainId: {}, seatId: {}, user: {}", request.getTrainId(), request.getSeatId(), userEmail);
        return bookingService.createBooking(request, userEmail);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        log.info("Get booking request received for id: {}", id);
        return bookingService.getBookingById(id);
    }
    
    @GetMapping("/user")
    public List<Booking> getUserBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("Get user bookings request received for user: {}", userEmail);
        return bookingService.getBookingsByUserEmail(userEmail);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getAllBookings() {
        log.info("Get all bookings request received");
        return bookingService.getAllBookings();
    }
    
    @PutMapping("/confirm/{bookingId}")
    public void confirmBooking(@PathVariable Long bookingId, @RequestParam String paymentId) {
        log.info("Confirm booking request received for bookingId: {}, paymentId: {}", bookingId, paymentId);
        bookingService.confirmBooking(bookingId, paymentId);
    }
    
    @PutMapping("/cancel/{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        log.info("Cancel booking request received for bookingId: {}", bookingId);
        bookingService.cancelBooking(bookingId);
    }
}