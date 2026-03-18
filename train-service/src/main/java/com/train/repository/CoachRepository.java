package com.train.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.common.enums.CoachType;
import com.train.entity.Coach;

public interface CoachRepository extends JpaRepository<Coach, Long> {

    List<Coach> findByTrain_Id(Long trainId);

    @Query("SELECT SUM(c.totalSeats) FROM Coach c WHERE c.train.id = :trainId AND c.coachType = :coachType")
    Integer sumTotalSeatsByTrainIdAndCoachType(@Param("trainId") Long trainId, @Param("coachType") CoachType coachType);

    @Query("SELECT DISTINCT c.coachType FROM Coach c WHERE c.train.id = :trainId ORDER BY c.coachType")
    List<CoachType> findDistinctCoachTypesByTrainId(@Param("trainId") Long trainId);
}
