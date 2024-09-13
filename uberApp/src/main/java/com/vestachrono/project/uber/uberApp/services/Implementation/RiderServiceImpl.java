package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.RideDto;
import com.vestachrono.project.uber.uberApp.dto.RideRequestDto;
import com.vestachrono.project.uber.uberApp.dto.RiderDto;
import com.vestachrono.project.uber.uberApp.entities.*;
import com.vestachrono.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.vestachrono.project.uber.uberApp.entities.enums.RideStatus;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.repositories.RideRequestRepository;
import com.vestachrono.project.uber.uberApp.repositories.RiderRepository;
import com.vestachrono.project.uber.uberApp.services.DriverService;
import com.vestachrono.project.uber.uberApp.services.RatingService;
import com.vestachrono.project.uber.uberApp.services.RideService;
import com.vestachrono.project.uber.uberApp.services.RiderService;
import com.vestachrono.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
//        get the current rider(user)
        Rider rider = getCurrentRider();
//        get the details from rideRequestDTO, create a rideRequest and map it to RideRequest entity
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
//        set the rideRequestStatus to PENDING for the driver to accept the rideRequest
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
//        assign the ride to the currentRider as it is created by him
        rideRequest.setRider(rider);
//        calculate the fare using rideStrategyManager.rideFareCalculationStrategy()
        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);
//        save the rideRequest to the rideRequestRepository
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);
//        match the driver to the ride using rideStrategyManager.driverMatchingStrategy()
        List<Driver> drivers = rideStrategyManager
                .driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);

//        TODO: Send notification to all the drivers about the ride request.

        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
//        Get the Current rider details
        Rider rider = getCurrentRider();
//        Get the ride details
        Ride ride = rideService.getRideById(rideId);
//        Check if the current rider owns the ride
        if (!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider does not own the ride: " + rideId);
        }
//        check if the ride is ongoing(started) only cancel when confirmed !(ongoing or ended)
        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride can not be cancelled, Invalid Status " + ride.getRideStatus());
        }
//        Update the rideStatus as cancelled
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.CANCELLED);
//        update driver availability, get the driver details from here
        driverService.updateDriverAvailability(ride.getDriver(), true);

//        return the ride profile by mapping it to RideDTO
        return modelMapper.map(savedRide, RideDto.class);

    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
//        get the ride
        Ride ride = rideService.getRideById(rideId);
//        get the current rider
        Rider rider = getCurrentRider();
//        check if the rider owns the ride
        if (!rider.equals(ride.getRider())) {
            throw new RuntimeException("Rider is not the owner of the ride ");
        }
//        check if the ride status is ended
        if (!ride.getRideStatus().equals(RideStatus.ENDED)) {
            throw new RuntimeException("Ride status is not ended, cannot rate the driver "+ride.getRideStatus());
        }
//        provide rating to the driver
        return ratingService.rateDriver(ride, rating);
    }

    @Override
    public RiderDto getMyProfile() {
//        get the current rider
        Rider currentRider = getCurrentRider();
//        map the currentRider to RiderDto
        return modelMapper.map(currentRider, RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
//        get the current rider
        Rider currentRider = getCurrentRider();
/*        get the metadata of the rides the driver made and return it as a RideDto as a pageable form
*         use map function to convert from one page to another */
        return rideService.getAllRidesOfRider(currentRider, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Rider createNewRider(User user) {
        Rider rider = Rider
                .builder()
                .user(user)
                .rating(0.0)
                .build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return riderRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(
                "Rider not associated to user with ID " + user.getId()
        ));
    }
}
