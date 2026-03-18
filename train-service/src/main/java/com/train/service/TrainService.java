package com.train.service;

import java.util.List;

import com.common.enums.CoachType;
import com.train.dto.TrainFareResponse;
import com.train.dto.TrainRequest;
import com.train.dto.TrainResponse;

public interface TrainService {
    TrainResponse addTrain(TrainRequest request);
    List<TrainResponse> getAllTrains();
    TrainResponse updateTrain(Long id, TrainRequest request);
    void deleteTrain(Long id);
    List<String> getAllStations();
    List<String> searchStations(String query);
    List<TrainResponse> searchTrains(String source, String destination);
    TrainFareResponse getTrainWithFare(Long trainId, CoachType coachType);
    TrainResponse getTrainById(Long trainId);
    Integer getTotalSeatsByCoachType(Long trainId, CoachType coachType);
    Integer getAvailableSeatsByCoachType(Long trainId, CoachType coachType, String date);
}
