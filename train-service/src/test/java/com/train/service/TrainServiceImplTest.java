package com.train.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.train.client.FareClient;
import com.train.dto.FareResponse;
import com.train.dto.TrainFareResponse;
import com.train.dto.TrainRequest;
import com.train.dto.TrainResponse;
import com.train.entity.Train;
import com.train.exception.ResourceNotFoundException;
import com.train.repository.TrainRepository;
import com.train.service.Impl.TrainServiceImpl;

@ExtendWith(MockitoExtension.class)
class TrainServiceImplTest {

    @Mock
    private TrainRepository trainRepository;

    @Mock
    private FareClient fareClient;

    @InjectMocks
    private TrainServiceImpl trainService;

    @Test
    void addTrain_Success() {
        TrainRequest request = new TrainRequest();
        request.setTrainNumber("12345");
        request.setName("Express");
        request.setSource("Delhi");
        request.setDestination("Mumbai");

        Train train = new Train();
        train.setId(1L);
        train.setTrainNumber("12345");
        train.setName("Express");
        train.setSource("Delhi");
        train.setDestination("Mumbai");

        when(trainRepository.save(any(Train.class))).thenReturn(train);

        TrainResponse response = trainService.addTrain(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Express", response.getName());
    }

    @Test
    void getAllTrains_Success() {
        Train train1 = new Train();
        train1.setId(1L);
        train1.setTrainNumber("12345");
        train1.setName("Express");
        train1.setSource("Delhi");
        train1.setDestination("Mumbai");

        Train train2 = new Train();
        train2.setId(2L);
        train2.setTrainNumber("67890");
        train2.setName("Superfast");
        train2.setSource("Chennai");
        train2.setDestination("Bangalore");

        when(trainRepository.findAll()).thenReturn(Arrays.asList(train1, train2));

        List<TrainResponse> responses = trainService.getAllTrains();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Express", responses.get(0).getName());
    }

    @Test
    void getTrainWithFare_Success() {
        Train train = new Train();
        train.setId(1L);
        train.setTrainNumber("12345");
        train.setName("Express");
        train.setSource("Delhi");
        train.setDestination("Mumbai");

        FareResponse fareResponse = new FareResponse();
        fareResponse.setAmount(1500.0);

        when(trainRepository.findById(anyLong())).thenReturn(Optional.of(train));
        when(fareClient.getFare(anyLong(), anyString())).thenReturn(fareResponse);

        TrainFareResponse response = trainService.getTrainWithFare(1L, "AC");

        assertNotNull(response);
        assertEquals("Express", response.getName());
        assertEquals(1500.0, response.getFareAmount());
    }

    @Test
    void getTrainWithFare_TrainNotFound() {
        when(trainRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
                () -> trainService.getTrainWithFare(1L, "AC"));
    }
}
