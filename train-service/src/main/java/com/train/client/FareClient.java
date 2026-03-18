package com.train.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.train.dto.FareResponse;

@FeignClient(name = "fare-service")
public interface FareClient {

    @GetMapping("/fare")
    FareResponse getFare(
        @RequestParam Long trainId,
        @RequestParam String coachType
    );
}