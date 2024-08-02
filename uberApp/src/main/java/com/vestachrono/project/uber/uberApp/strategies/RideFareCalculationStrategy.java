package com.vestachrono.project.uber.uberApp.strategies;

import com.vestachrono.project.uber.uberApp.dto.RideRequestDto;

public interface RideFareCalculationStrategy {

    double calculateFare(RideRequestDto rideRequestDto);
}
