package com.vestachrono.project.uber.uberApp.repositories;

import com.vestachrono.project.uber.uberApp.entities.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiderRequestRepository extends JpaRepository<RideRequest, Long> {
}
