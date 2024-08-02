package com.vestachrono.project.uber.uberApp.strategies.implementation;

import com.vestachrono.project.uber.uberApp.dto.RideRequestDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.strategies.DriverMatchingStrategy;

import java.util.List;

public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    @Override
    public List<Driver> findMatchingDriver(RideRequestDto rideRequestDto) {
        return List.of();
    }
}
