package com.fare.entity;

import com.common.enums.CoachType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fare")
@Getter
@Setter
@NoArgsConstructor
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long trainId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachType coachType;

    @NotNull
    @Positive
    @Column(name = "base_fare", nullable = false)
    private Double baseFare;
}
