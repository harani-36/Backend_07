package com.fare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.common.enums.CoachType;
import com.fare.entity.Fare;

public interface FareRepository extends JpaRepository<Fare, Long> {
    Optional<Fare> findByTrainIdAndCoachType(Long trainId, CoachType coachType);

    @Query("SELECT f.coachType FROM Fare f WHERE f.trainId = :trainId ORDER BY f.coachType")
    List<CoachType> findCoachTypesByTrainId(@Param("trainId") Long trainId);
}
