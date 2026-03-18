package com.search.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.search.client.TrainClient;
import com.search.dto.TrainResponse;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainClient trainClient;

    private List<TrainResponse> trainResponses;

    @BeforeEach
    void setUp() {
        TrainResponse train1 = new TrainResponse();
        train1.setId(1L);
        train1.setTrainNumber("12345");
        train1.setName("Express");
        train1.setSource("Mumbai");
        train1.setDestination("Delhi");

        TrainResponse train2 = new TrainResponse();
        train2.setId(2L);
        train2.setTrainNumber("67890");
        train2.setName("Superfast");
        train2.setSource("Mumbai");
        train2.setDestination("Delhi");

        trainResponses = Arrays.asList(train1, train2);
    }

    @Test
    void searchTrains_Success() throws Exception {
        when(trainClient.getAllTrains()).thenReturn(trainResponses);

        mockMvc.perform(get("/search")
                .param("source", "Mumbai")
                .param("destination", "Delhi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainNumber").value("12345"))
                .andExpect(jsonPath("$[1].trainNumber").value("67890"));

        verify(trainClient, times(1)).getAllTrains();
    }
}
