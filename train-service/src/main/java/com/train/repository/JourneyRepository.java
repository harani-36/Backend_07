package com.train.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.train.entity.Journey;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    List<Journey> findByTrainIdAndJourneyDate(Long trainId, LocalDate journeyDate);
    List<Journey> findByJourneyDate(LocalDate journeyDate);
}
