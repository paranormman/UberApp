package com.vestachrono.project.uber.uberApp.dto;

import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.Rider;
import com.vestachrono.project.uber.uberApp.entities.enums.PaymentMethod;
import com.vestachrono.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.vestachrono.project.uber.uberApp.entities.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDto {

    private Long id;

    private Point pickupLocation;

    private Point dropOffLocation;

    private LocalDateTime createdTime;

    private RiderDto rider;

    private DriverDto driver;

    private PaymentMethod paymentMethod;

    private RideRequestStatus rideRequestStatus;

    private RideStatus rideStatus;

    private String otp;

    private Double fare;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

}
