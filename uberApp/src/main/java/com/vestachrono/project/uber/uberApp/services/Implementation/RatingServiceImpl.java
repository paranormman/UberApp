package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.RiderDto;
import com.vestachrono.project.uber.uberApp.entities.*;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.repositories.DriverRepository;
import com.vestachrono.project.uber.uberApp.repositories.RatingRepository;
import com.vestachrono.project.uber.uberApp.repositories.RiderRepository;
import com.vestachrono.project.uber.uberApp.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;


    @Override
    public DriverDto rateDriver(Ride ride, Integer rating) {
//        getting driver from the ride
        Driver driver = ride.getDriver();
//       getting rating for the ride
        Rating ratingObject = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for the id: "+ ride.getId()));
//        check if the Driver is already rated
        if (ratingObject.getDriverRating() != null)
            throw new RuntimeException("Driver is already rated, Driver can not be rated again");
//        set the DriverRating(All previous Ratings)
        ratingObject.setDriverRating(rating);
//        save the rating in the ratingRepo
        ratingRepository.save(ratingObject);

//        from the rating repository map the rating to double using stream and find the average
        Double newRating = ratingRepository.findByDriver(driver)
                .stream()
                .mapToDouble(rating1 -> rating1.getDriverRating())
                .average().orElse(0.0);
//        set this newRating to the Driver
        driver.setRating(newRating);
//        save the driver with the updated rating
        Driver savedDriver = driverRepository.save(driver);

        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public void createNewRating(Ride ride) {
//        create new rating object when the ride is started
        Rating rating = Rating.builder()
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .ride(ride)
                .build();
        ratingRepository.save(rating);
    }

    @Override
    public RiderDto rateRider(Ride ride, Integer rating) {
        Rider rider = ride.getRider();
//        getting rating for the ride
        Rating ratingObject = ratingRepository.findByRide(ride)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for id " + ride.getId()));
//        check if the Rider is already rated
        if (ratingObject.getRiderRating() != null)
            throw new RuntimeException("Rider is already rated, cannot be rated again");
//        set the rider rating
        ratingObject.setRiderRating(rating);
//        save the rating in the rating repo
        ratingRepository.save(ratingObject);

//        from the rating repository map the rating to double using stream and find the average
        Double newRating = ratingRepository.findByRider(rider)
                .stream()
                .mapToDouble(rating1 -> rating1.getRiderRating())
                .average().orElse(0.0);
//        set the new rating to the rider
        rider.setRating(newRating);
//        save the rider with the updated rating
        Rider savedRider = riderRepository.save(rider);
//        map the savedRider to RiderDto
        return modelMapper.map(savedRider, RiderDto.class);

    }
}
