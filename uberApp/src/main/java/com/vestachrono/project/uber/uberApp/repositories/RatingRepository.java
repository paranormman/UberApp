package com.vestachrono.project.uber.uberApp.repositories;

import com.vestachrono.project.uber.uberApp.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

//    @Query(value = "SELECT COUNT(r) FROM Rating r WHERE r.user.id", nativeQuery = true)
//    int countRatingsByUserId(Long userId);
//
//    @Query(value = "SELECT AVG(r.rating) FROM Rating r WHERE r.user.id", nativeQuery = true)
//    double avgRatingsByUserId(Long userId);

}
