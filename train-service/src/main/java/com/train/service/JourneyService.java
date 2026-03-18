package com.train.service;

import java.time.LocalDate;
import java.util.List;

import com.train.dto.JourneyRequest;
import com.train.dto.JourneyResponse;

public interface JourneyService {
    JourneyResponse createJourney(JourneyRequest request);
    List<JourneyResponse> getAllJourneys();
    JourneyResponse getJourneyById(Long id);
    List<JourneyResponse> getJourneysByDate(LocalDate date);
    void cancelJourney(Long journeyId);
}
