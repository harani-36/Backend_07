package com.train.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.train.entity.Seat;
import com.train.entity.SeatStatus;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findSeatForUpdate(@Param("id") Long id);

    // FIXED: coach now belongs to train directly; seat has journeyId
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s JOIN s.coach c WHERE c.train.id = :trainId AND c.coachType = :coachType AND s.status = 'AVAILABLE' ORDER BY s.seatNumber")
    Optional<Seat> findFirstAvailableSeatByTrainAndCoach(@Param("trainId") Long trainId, @Param("coachType") String coachType);

    @Query("SELECT s FROM Seat s JOIN s.coach c WHERE c.train.id = :trainId AND c.coachType = :coachType AND s.status = 'AVAILABLE' ORDER BY s.seatNumber")
    List<Seat> findAvailableSeatsByTrainAndCoach(@Param("trainId") Long trainId, @Param("coachType") String coachType);

    // Count available seats per train + coachType + journeyId
    @Query("SELECT COUNT(s) FROM Seat s JOIN s.coach c WHERE c.train.id = :trainId AND c.coachType = :coachType AND s.journeyId = :journeyId AND s.status = :status")
    Integer countByTrainCoachJourneyAndStatus(@Param("trainId") Long trainId, @Param("coachType") String coachType, @Param("journeyId") Long journeyId, @Param("status") SeatStatus status);

    // Used by JourneyServiceImpl for seat counts and cancellation
    int countByJourneyId(Long journeyId);

    int countByJourneyIdAndStatus(Long journeyId, SeatStatus status);

    @Transactional
    void deleteByJourneyId(Long journeyId);
}
