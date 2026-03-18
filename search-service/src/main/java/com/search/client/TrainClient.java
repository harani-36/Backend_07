package com.search.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.search.dto.TrainResponse;

@FeignClient(name = "train-service", contextId = "trainClient")
public interface TrainClient {

    @GetMapping("/trains")
    List<TrainResponse> getAllTrains();

    @GetMapping("/trains/stations")
    List<String> getAllStations();

    @GetMapping("/trains/search")
    List<TrainResponse> searchTrains(@RequestParam String source, @RequestParam String destination);
}
