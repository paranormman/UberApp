package com.vestachrono.project.uber.uberApp.strategies.implementation;

import com.vestachrono.project.uber.uberApp.dto.RideRequestDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.RideRequest;
import com.vestachrono.project.uber.uberApp.repositories.DriverRepository;
import com.vestachrono.project.uber.uberApp.strategies.DriverMatchingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {

    private final DriverRepository driverRepository;

    @Override
    public List<Driver> findMatchingDrivers(RideRequest rideRequest) {

        return driverRepository.findMatchingDrivers(rideRequest.getPickupLocation());
    }
}
