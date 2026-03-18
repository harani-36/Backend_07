package com.booking.service;

import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.entity.Booking;

import java.util.List;

public interface BookingService {
	BookingResponse createBooking(BookingRequest request, String userEmail);
	Booking getBookingById(Long id);
	List<Booking> getBookingsByUserEmail(String userEmail);
	List<Booking> getAllBookings();
	void confirmBooking(Long bookingId, String paymentId);
	void cancelBooking(Long bookingId);
}
