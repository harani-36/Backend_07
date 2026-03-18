package com.booking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.booking.client.PaymentClient;
import com.booking.client.TrainClient;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.PaymentOrderRequest;
import com.booking.dto.PaymentOrderResponse;
import com.booking.entity.Booking;
import com.booking.entity.BookingStatus;
import com.booking.exception.ResourceNotFoundException;
import com.booking.repository.BookingRepository;
import com.booking.service.BookingService;
import com.common.enums.CoachType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final TrainClient trainClient;
    private final PaymentClient paymentClient;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        log.debug("Starting booking creation for trainId: {}, user: {}", request.getTrainId(), userEmail);

        Long assignedSeatId;
        
        try {
            if (request.getSeatId() != null) {
                // Manual seat selection (if provided)
                log.info("Locking manually selected seat: {}", request.getSeatId());
                trainClient.lockSeat(request.getSeatId());
                assignedSeatId = request.getSeatId();
            } else {
                // Auto-assign seat based on coach type
                log.info("Auto-assigning seat for trainId: {}, coachType: {}", request.getTrainId(), request.getCoachType());
                assignedSeatId = trainClient.autoAssignSeat(request.getTrainId(), request.getCoachType().name());
                log.info("Auto-assigned seat: {}", assignedSeatId);
            }
        } catch (Exception e) {
            log.error("Failed to assign seat for trainId: {}, coachType: {}: {}", request.getTrainId(), request.getCoachType(), e.getMessage());
            throw new RuntimeException("No available seats in " + request.getCoachType().name() + " coach", e);
        }

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setUserEmail(userEmail);
        booking.setJourneyId(request.getJourneyId());
        booking.setTrainId(request.getTrainId());
        booking.setSeatId(assignedSeatId);
        booking.setPassengerName(request.getPassengerName());
        booking.setCoachType(request.getCoachType());
        booking.setFare(request.getFare());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with PENDING status, id: {}, assignedSeat: {}", savedBooking.getId(), assignedSeatId);

        // Create Razorpay order
        PaymentOrderRequest orderRequest = new PaymentOrderRequest(savedBooking.getId(), request.getFare());
        PaymentOrderResponse orderResponse = paymentClient.createOrder(orderRequest);
        log.info("Razorpay order created: {}", orderResponse.getRazorpayOrderId());

        BookingResponse response = new BookingResponse();
        response.setBookingId(savedBooking.getId());
        response.setUserId(savedBooking.getUserId());
        response.setJourneyId(savedBooking.getJourneyId());
        response.setTrainId(savedBooking.getTrainId());
        response.setSeatId(savedBooking.getSeatId());
        response.setPassengerName(savedBooking.getPassengerName());
        response.setCoachType(savedBooking.getCoachType());
        response.setFare(savedBooking.getFare());
        response.setBookingStatus(savedBooking.getBookingStatus());
        response.setRazorpayOrderId(orderResponse.getRazorpayOrderId());
        response.setRazorpayKeyId(orderResponse.getRazorpayKeyId());

        return response;
    }

    @Override
    public Booking getBookingById(Long id) {
        log.debug("Fetching booking with id: {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Booking not found with id: {}", id);
                    return new ResourceNotFoundException("Booking not found with ID: " + id);
                });
    }
    
    @Override
    public List<Booking> getBookingsByUserEmail(String userEmail) {
        log.debug("Fetching bookings for user: {}", userEmail);
        return bookingRepository.findByUserEmailOrderByBookingTimeDesc(userEmail);
    }
    
    @Override
    public List<Booking> getAllBookings() {
        log.debug("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Transactional
    @Override
    public void confirmBooking(Long bookingId, String paymentId) {
        log.info("Confirming booking: {}", bookingId);
        Booking booking = getBookingById(bookingId);
        
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            log.warn("Booking {} is not in PENDING status", bookingId);
            throw new IllegalStateException("Booking is not in PENDING status");
        }
        
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(paymentId);
        bookingRepository.save(booking);
        
        // Book the seat permanently
        trainClient.bookSeat(booking.getSeatId());
        log.info("Booking {} confirmed successfully", bookingId);
    }

    @Transactional
    @Override
    public void cancelBooking(Long bookingId) {
        log.info("Cancelling booking: {}", bookingId);
        Booking booking = getBookingById(bookingId);
        
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            log.warn("Cannot cancel confirmed booking: {}", bookingId);
            throw new IllegalStateException("Cannot cancel confirmed booking");
        }
        
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Unlock the seat
        trainClient.unlockSeat(booking.getSeatId());
        log.info("Booking {} cancelled successfully", bookingId);
    }
}