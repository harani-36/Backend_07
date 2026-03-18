package com.train.service;

import java.util.List;

import com.common.enums.CoachType;

public interface CoachService {
    List<CoachType> getAvailableCoachTypes(Long trainId);
}
