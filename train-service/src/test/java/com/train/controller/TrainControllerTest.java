package com.train.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.train.config.SecurityConfig;
import com.train.dto.TrainFareResponse;
import com.train.dto.TrainRequest;
import com.train.dto.TrainResponse;
import com.train.entity.Seat;
import com.train.entity.SeatStatus;
import com.train.exception.ResourceNotFoundException;
import com.train.exception.SeatAlreadyBookedException;
import com.train.repository.SeatRepository;
import com.train.service.TrainService;
import com.train.util.JwtUtil;

@WebMvcTest(TrainController.class)
@Import(SecurityConfig.class)
class TrainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainService trainService;

    @MockBean
    private SeatRepository seatRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addTrain_Success() throws Exception {
        TrainRequest request = new TrainRequest();
        request.setTrainNumber("12345");
        request.setName("Express");
        request.setSource("Delhi");
        request.setDestination("Mumbai");

        TrainResponse response = new TrainResponse(1L, "12345", "Express", "Delhi", "Mumbai");
        when(trainService.addTrain(any(TrainRequest.class))).thenReturn(response);

        mockMvc.perform(post("/trains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Express"));
    }

    @Test
    @WithMockUser
    void addTrain_Forbidden() throws Exception {
        TrainRequest request = new TrainRequest();
        request.setTrainNumber("12345");
        request.setName("Express");
        request.setSource("Delhi");
        request.setDestination("Mumbai");

        mockMvc.perform(post("/trains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getAllTrains_Success() throws Exception {
        TrainResponse response = new TrainResponse(1L, "12345", "Express", "Delhi", "Mumbai");
        when(trainService.getAllTrains()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Express"));
    }

    @Test
    @WithMockUser
    void getTrainWithFare_Success() throws Exception {
        TrainFareResponse response = new TrainFareResponse(1L, "Express", "Delhi", "Mumbai", "AC", 1000.0);
        when(trainService.getTrainWithFare(anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(get("/trains/1/fare")
                .param("coachType", "AC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fareAmount").value(1000.0));
    }

    @Test
    @WithMockUser
    void bookSeat_Success() throws Exception {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setStatus(SeatStatus.AVAILABLE);

        when(seatRepository.findSeatForUpdate(anyLong())).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        mockMvc.perform(put("/trains/seats/1/book"))
                .andExpect(status().isOk());

        verify(seatRepository).save(any(Seat.class));
    }

    @Test
    @WithMockUser
    void bookSeat_SeatNotFound() throws Exception {
        when(seatRepository.findSeatForUpdate(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/trains/seats/1/book"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void bookSeat_AlreadyBooked() throws Exception {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setStatus(SeatStatus.BOOKED);

        when(seatRepository.findSeatForUpdate(anyLong())).thenReturn(Optional.of(seat));

        mockMvc.perform(put("/trains/seats/1/book"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTrains_Unauthorized() throws Exception {
        mockMvc.perform(get("/trains"))
                .andExpect(status().isUnauthorized());
    }
}
