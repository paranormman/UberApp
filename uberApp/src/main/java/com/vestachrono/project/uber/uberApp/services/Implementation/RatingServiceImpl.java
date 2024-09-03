package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.entities.*;
import com.vestachrono.project.uber.uberApp.repositories.RatingRepository;
import com.vestachrono.project.uber.uberApp.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

//    private final RatingRepository ratingRepository;
//
//    @Override
//    public double calculateAverageRiderRating(Rider rider, double rating) {
//        long count = ratingRepository.countRatingsByUserId(rider.getId());
//        double currentAverageRating = ratingRepository.avgRatingsByUserId(rider.getId());
//
//        return (currentAverageRating * count + rating) / (count + 1);
//    }
//
//    @Override
//    public double calculateAverageDriverRating(Driver driver, double rating) {
//        long count = ratingRepository.countRatingsByUserId(driver.getId());
//        double currentAverageRating = ratingRepository.avgRatingsByUserId(driver.getId());
//
//        return (currentAverageRating * count + rating) / (count + 1);
//    }


//    @Override
//    public void updateRating(Ride ride, double rating) {
//        ride.getRider().setRating(rating);
//        ratingRepository.save(ride.getRider().setRating(rating));
//    }
}
