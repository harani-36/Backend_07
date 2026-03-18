package com.fare.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fare.dto.FareRequest;
import com.fare.dto.FareResponse;
import com.fare.service.FareService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FareController.class)
class FareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FareService fareService;

    @Autowired
    private ObjectMapper objectMapper;

    private FareRequest fareRequest;
    private FareResponse fareResponse;

    @BeforeEach
    void setUp() {
        fareRequest = new FareRequest();
        fareRequest.setTrainId(1L);
        fareRequest.setCoachType("AC");
        fareRequest.setAmount(1200.0);

        fareResponse = new FareResponse(1L, "AC", 1200.0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addFare_Success() throws Exception {
        when(fareService.addFare(any(FareRequest.class))).thenReturn(fareResponse);

        mockMvc.perform(post("/fare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fareRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainId").value(1L))
                .andExpect(jsonPath("$.coachType").value("AC"))
                .andExpect(jsonPath("$.amount").value(1200.0));

        verify(fareService, times(1)).addFare(any(FareRequest.class));
    }

    @Test
    @WithMockUser
    void getFare_Success() throws Exception {
        when(fareService.getFare(1L, "AC")).thenReturn(fareResponse);

        mockMvc.perform(get("/fare")
                .param("trainId", "1")
                .param("coachType", "AC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainId").value(1L))
                .andExpect(jsonPath("$.coachType").value("AC"))
                .andExpect(jsonPath("$.amount").value(1200.0));

        verify(fareService, times(1)).getFare(1L, "AC");
    }
}
