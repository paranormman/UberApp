package com.vestachrono.project.uber.uberApp.strategies;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.RideRequestDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;

import java.util.List;

public interface DriverMatchingStrategy {

    List<Driver> findMatchingDriver(RideRequestDto rideRequestDto);
}
