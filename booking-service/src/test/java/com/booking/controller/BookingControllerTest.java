package com.booking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.booking.config.SecurityConfig;
import com.booking.dto.BookingRequest;
import com.booking.entity.Booking;
import com.booking.exception.ResourceNotFoundException;
import com.booking.exception.SeatAlreadyBookedException;
import com.booking.service.BookingService;
import com.booking.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createBooking_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setTrainId(1L);
        request.setSeatId(10L);
        request.setPassengerName("John Doe");
        request.setCoachType("AC");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTrainId(1L);
        booking.setSeatId(10L);
        booking.setPassengerName("John Doe");
        booking.setCoachType("AC");
        booking.setBookingTime(LocalDateTime.now());

        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(booking);

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.passengerName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void createBooking_SeatAlreadyBooked() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setTrainId(1L);
        request.setSeatId(10L);
        request.setPassengerName("John Doe");
        request.setCoachType("AC");

        when(bookingService.createBooking(any(BookingRequest.class)))
                .thenThrow(new SeatAlreadyBookedException("Seat already booked"));

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createBooking_ValidationFailure() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setPassengerName("");

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getBookingById_Success() throws Exception {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTrainId(1L);
        booking.setSeatId(10L);
        booking.setPassengerName("John Doe");
        booking.setCoachType("AC");

        when(bookingService.getBookingById(anyLong())).thenReturn(booking);

        mockMvc.perform(get("/booking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.passengerName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getBookingById_NotFound() throws Exception {
        when(bookingService.getBookingById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Booking not found with ID: 1"));

        mockMvc.perform(get("/booking/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_Unauthorized() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setTrainId(1L);
        request.setSeatId(10L);
        request.setPassengerName("John Doe");
        request.setCoachType("AC");

        mockMvc.perform(post("/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
