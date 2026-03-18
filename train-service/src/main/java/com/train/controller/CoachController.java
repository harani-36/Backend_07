package com.train.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.enums.CoachType;
import com.train.service.CoachService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coaches")
@RequiredArgsConstructor
public class CoachController {

    private static final Logger log = LoggerFactory.getLogger(CoachController.class);
    private final CoachService coachService;

    @GetMapping("/train/{trainId}")
    public List<CoachType> getAvailableCoachTypes(@PathVariable Long trainId) {
        log.info("Get available coach types request for train ID: {}", trainId);
        return coachService.getAvailableCoachTypes(trainId);
    }
}
