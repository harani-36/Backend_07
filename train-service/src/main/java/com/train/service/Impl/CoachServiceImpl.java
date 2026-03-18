package com.train.service.Impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.common.enums.CoachType;
import com.train.repository.CoachRepository;
import com.train.service.CoachService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoachServiceImpl implements CoachService {

    private static final Logger log = LoggerFactory.getLogger(CoachServiceImpl.class);
    private final CoachRepository coachRepository;

    @Override
    public List<CoachType> getAvailableCoachTypes(Long trainId) {
        log.info("Fetching available coach types for train ID: {}", trainId);
        List<CoachType> coachTypes = coachRepository.findDistinctCoachTypesByTrainId(trainId);
        log.info("Found {} coach types for train ID: {}", coachTypes.size(), trainId);
        return coachTypes;
    }
}
