package com.vestachrono.project.uber.uberApp.services;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.RiderDto;
import com.vestachrono.project.uber.uberApp.entities.*;

public interface RatingService {

    RiderDto rateRider(Ride ride, Integer rating);
    DriverDto rateDriver(Ride ride, Integer rating);

    void createNewRating(Ride ride);
}
