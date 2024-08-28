package com.vestachrono.project.uber.uberApp.dto;

import com.vestachrono.project.uber.uberApp.entities.enums.PaymentMethod;
import com.vestachrono.project.uber.uberApp.entities.enums.RideRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    private Long id;
    private PointDto pickupLocation;

    private PointDto dropOffLocation;

    private LocalDateTime requestedTime;
    private Double fare;

    private RiderDto rider;

    private PaymentMethod paymentMethod;

    private RideRequestStatus rideRequestStatus;

}
