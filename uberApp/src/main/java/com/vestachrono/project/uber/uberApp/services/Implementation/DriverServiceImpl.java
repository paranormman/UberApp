package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.DriverDto;
import com.vestachrono.project.uber.uberApp.dto.RideDto;
import com.vestachrono.project.uber.uberApp.dto.RiderDto;
import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.Ride;
import com.vestachrono.project.uber.uberApp.entities.RideRequest;
import com.vestachrono.project.uber.uberApp.entities.Rider;
import com.vestachrono.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.vestachrono.project.uber.uberApp.entities.enums.RideStatus;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.repositories.DriverRepository;
import com.vestachrono.project.uber.uberApp.repositories.RatingRepository;
import com.vestachrono.project.uber.uberApp.repositories.RiderRepository;
import com.vestachrono.project.uber.uberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;


    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {
//        Create a new rideRequest and check if it already exists in the repo
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);
//        Check the status of requestedRide, ride cannot be accepted if the status is anything other than pending
        if (!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RuntimeException("RideRequest can not be accepted, status is "  + rideRequest.getRideRequestStatus());
        }
//        get the currentDriver and check availability
        Driver currentDriver = getCurrentDriver();
        if (!currentDriver.getAvailable()) {
            throw new RuntimeException("Driver cannot accept the ride due to unavailability");
        }
//        once the ride is accepted set the driver availability to false
        Driver savedDriver = updateDriverAvailability(currentDriver, false);
//        Create a new ride using the rideRequest and savedDriver details created above
        Ride ride = rideService.createNewRide(rideRequest, savedDriver);
//        return the ride profile by mapping it to RideDto
        return modelMapper.map(ride, RideDto.class);

    }

    @Override
    public RideDto startRide(Long rideId, String otp) {
//        Get the ride details
        Ride ride = rideService.getRideById(rideId);
//        Get the current driver
        Driver driver = getCurrentDriver();
//        Check if the driver owns the ride (currentDriver == driver(ride))
        if (!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver can not start the ride as he did not accept the ride");
        }
/*        check the ride status throw error message for status anything other than CONFIRMED,
*         status can not be Ongoing, Cancelled or Ended for the ride to be accepted */

        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride status is not confirmed, the ride can not be started, status: " + ride.getRideStatus());
        }
//        check if the otp is valid
        if (!otp.equals(ride.getOtp())) {
            throw new RuntimeException("OTP is invalid " + otp);
        }
//        Change the status to ONGOING to indicate the ride is started by creating a saveRide object
        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ONGOING);

//        Create a payment object
        paymentService.createNewPayment(savedRide);

//        return the ride profile by mapping it to RideDto
        return modelMapper.map(savedRide, RideDto.class);

    }

    @Override
    public RideDto cancelRide(Long rideId) {
//        get the ride details
        Ride ride = rideService.getRideById(rideId);
//        get the current driver
        Driver driver = getCurrentDriver();
//        check if the driver owns the ride
        if (!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot Start the ride as he did not accept the ride");
        }
//        check if the ride is ongoing(started) only cancel when confirmed !(ongoing or ended)
        if (!ride.getRideStatus().equals(RideStatus.CONFIRMED)) {
            throw new RuntimeException("Ride cannot be cancelled, Invalid status: "+ ride.getRideStatus());
        }
//        update the ride status as CANCELLED
        rideService.updateRideStatus(ride, RideStatus.CANCELLED);
//        Set the driver availability to true from false
        updateDriverAvailability(driver, true);

//        return the ride profile by mapping it to RideDto
        return modelMapper.map(ride, RideDto.class);
    }

    @Override
    public RideDto endRide(Long rideId) {
//        get ride details
        Ride ride = rideService.getRideById(rideId);
//        get Driver details
        Driver driver = getCurrentDriver();
//        check if driver owns the ride
        if (!driver.equals(ride.getDriver())) {
            throw new RuntimeException("Driver cannot END the ride as he has not started the ride");
        }
//        check if ride is Ongoing(started) only end when ongoing
        if (!ride.getRideStatus().equals(RideStatus.ONGOING)) {
            throw new RuntimeException("Ride cannot be ended, Invalid status "+ride.getRideStatus());
        }
//        update the endTime
        ride.setEndedAt(LocalDateTime.now());
//        update the ride status to ENDED
        Ride savedRide = rideService.updateRideStatus(ride, RideStatus.ENDED);
//        free the driver by updating driver availability to true
        updateDriverAvailability(driver, true);

//        process the payment for this ride
        paymentService.processPayment(ride);

//        return the ride profile by mapping it to RideDto
        return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
//        get the rider assigned to the ride
//        Rider rider = rideService.getRideById(ride.getRider().getId()).getRider();
//
//        double riderAverageRating = ratingService.calculateAverageRiderRating(rider, rating);
//
//        ratingService.updateRating(ride, riderAverageRating);
//
//        return modelMapper.map(riderAverageRating, RideDto.class);
        return null;
    }

    @Override
    public DriverDto getMyProfile() {
//        get the currentDriver
        Driver currentDriver = getCurrentDriver();
//        get the driver profile by mapping it to DriverDto(This will return all the variables defined in the DriverDTO)
        return modelMapper.map(currentDriver, DriverDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {
//        get the current driver to get driver id
        Driver currentDriver = getCurrentDriver();
/*        get the metadata of the rides the driver made and return it as a RideDto as a pageable form
*         use map function to convert from one page to another */
        return rideService.getAllRidersOfDriver(currentDriver, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {
        return driverRepository.findById(2L)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with Id " + 2));
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
//        update the available status
        driver.setAvailable(available);
//        save the driver in the repository
        return driverRepository.save(driver);
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
