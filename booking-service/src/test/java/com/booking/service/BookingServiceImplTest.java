package com.booking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.booking.client.TrainClient;
import com.booking.dto.BookingRequest;
import com.booking.entity.Booking;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.SeatAlreadyBookedException;
import com.booking.repository.BookingRepository;
import com.booking.service.impl.BookingServiceImpl;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TrainClient trainClient;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequest bookingRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRequest = new BookingRequest();
        bookingRequest.setTrainId(1L);
        bookingRequest.setSeatId(10L);
        bookingRequest.setPassengerName("John Doe");
        bookingRequest.setCoachType("AC");

        booking = new Booking();
        booking.setId(1L);
        booking.setTrainId(1L);
        booking.setSeatId(10L);
        booking.setPassengerName("John Doe");
        booking.setCoachType("AC");
    }

    @Test
    void createBooking_Success() {
        doNothing().when(trainClient).bookSeat(anyLong());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(bookingRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getPassengerName());
        verify(trainClient).bookSeat(10L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_SeatAlreadyBooked() {
        Request request = Request.create(Request.HttpMethod.PUT, "/trains/seats/10/book",
                java.util.Collections.emptyMap(), null, new RequestTemplate());
        
        doThrow(new SeatAlreadyBookedException("Seat already booked"))
                .when(trainClient).bookSeat(anyLong());

        assertThrows(SeatAlreadyBookedException.class, () -> bookingService.createBooking(bookingRequest));
        verify(trainClient).bookSeat(10L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_TrainServiceFailure() {
        Request request = Request.create(Request.HttpMethod.PUT, "/trains/seats/10/book",
                java.util.Collections.emptyMap(), null, new RequestTemplate());
        
        doThrow(FeignException.class).when(trainClient).bookSeat(anyLong());

        assertThrows(FeignException.class, () -> bookingService.createBooking(bookingRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_Success() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getPassengerName());
    }

    @Test
    void getBookingById_NotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1L));
    }
}
