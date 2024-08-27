package com.vestachrono.project.uber.uberApp.strategies;

import com.vestachrono.project.uber.uberApp.strategies.implementation.DriverMatchingHighestRatedDriverStrategy;
import com.vestachrono.project.uber.uberApp.strategies.implementation.DriverMatchingNearestDriverStrategy;
import com.vestachrono.project.uber.uberApp.strategies.implementation.RideFareSurgePricingFareCalculationStrategy;
import com.vestachrono.project.uber.uberApp.strategies.implementation.RiderFareDefaultFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class RideStrategyManager {

    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final RideFareSurgePricingFareCalculationStrategy surgePricingFareCalculationStrategy;
    private final RiderFareDefaultFareCalculationStrategy defaultFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating) {
        if (riderRating > 4.8) {
            return highestRatedDriverStrategy;
        } else
            return nearestDriverStrategy;
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy() {

//        peak hours ranging from 6PM to 9PM
        LocalTime surgeStartTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);
        LocalTime currentTime = LocalTime.now();

        boolean isSurgeTime = currentTime.isAfter(surgeStartTime) && currentTime.isBefore(surgeEndTime);
        if (isSurgeTime) {
            return surgePricingFareCalculationStrategy;
        } else
            return defaultFareCalculationStrategy;
    }

}
