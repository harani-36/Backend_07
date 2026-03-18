package com.search.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.search.dto.JourneyResponse;

@FeignClient(name = "train-service", contextId = "journeyClient")
public interface JourneyClient {
    @GetMapping("/journeys/by-date")
    List<JourneyResponse> getJourneysByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
