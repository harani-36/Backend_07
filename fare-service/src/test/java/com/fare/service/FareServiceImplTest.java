package com.fare.service;

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

import com.fare.dto.FareRequest;
import com.fare.dto.FareResponse;
import com.fare.entity.Fare;
import com.fare.repository.FareRepository;
import com.fare.service.impl.FareServiceImpl;

@ExtendWith(MockitoExtension.class)
class FareServiceImplTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private FareServiceImpl fareService;

    private FareRequest fareRequest;
    private Fare fare;

    @BeforeEach
    void setUp() {
        fareRequest = new FareRequest();
        fareRequest.setTrainId(1L);
        fareRequest.setCoachType("AC");
        fareRequest.setAmount(1200.0);

        fare = new Fare();
        fare.setId(1L);
        fare.setTrainId(1L);
        fare.setCoachType("AC");
        fare.setAmount(1200.0);
    }

    @Test
    void addFare_Success() {
        when(fareRepository.save(any(Fare.class))).thenReturn(fare);

        FareResponse response = fareService.addFare(fareRequest);

        assertNotNull(response);
        assertEquals(1L, response.getTrainId());
        assertEquals("AC", response.getCoachType());
        assertEquals(1200.0, response.getAmount());
        verify(fareRepository, times(1)).save(any(Fare.class));
    }

    @Test
    void getFare_Success() {
        when(fareRepository.findByTrainIdAndCoachType(1L, "AC")).thenReturn(Optional.of(fare));

        FareResponse response = fareService.getFare(1L, "AC");

        assertNotNull(response);
        assertEquals(1L, response.getTrainId());
        assertEquals("AC", response.getCoachType());
        assertEquals(1200.0, response.getAmount());
        verify(fareRepository, times(1)).findByTrainIdAndCoachType(1L, "AC");
    }

    @Test
    void getFare_NotFound() {
        when(fareRepository.findByTrainIdAndCoachType(1L, "AC")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fareService.getFare(1L, "AC"));
        verify(fareRepository, times(1)).findByTrainIdAndCoachType(1L, "AC");
    }
}
