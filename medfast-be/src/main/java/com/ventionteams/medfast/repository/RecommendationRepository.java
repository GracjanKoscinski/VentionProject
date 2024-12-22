package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the recommendation entity.
 */
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

}
