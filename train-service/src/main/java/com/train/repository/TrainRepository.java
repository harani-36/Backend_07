package com.train.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.train.entity.Train;

public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByTrainNumber(String trainNumber);
    List<Train> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);
}
