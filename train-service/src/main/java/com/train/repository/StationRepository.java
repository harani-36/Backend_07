package com.train.repository;

import java.util.List;

import com.train.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StationRepository extends JpaRepository<Station, Long> {

    @Query("SELECT s FROM Station s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY s.name")
    List<Station> searchByNameOrCityOrCode(@Param("query") String query);
}
