package com.ventionteams.medfast.config.util.impl;

import com.ventionteams.medfast.config.util.api.EntityProvider;
import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Recommendation;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.repository.RecommendationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.hibernate.WrongClassException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Provides a recommendation entity for further integration testing.
 */
@Lazy
@Service
@RequiredArgsConstructor
public class RecommendationProvider implements EntityProvider<Recommendation> {

  private final RecommendationRepository recommendationRepository;
  private final Random random;

  /**
   * Provides a specialization entity.
   *
   * @return Recommendation
   */
  @Override
  @Transactional
  public Recommendation provide(List<Object> references) {
    int ageFrom = random.nextInt(30) + 18;
    int ageTo = ageFrom + 10;
    Recommendation recommendation = Recommendation.builder()
        .ageFrom(ageFrom)
        .ageTo(ageTo)
        .legalGender(Gender.MALE)
        .build();

    references.forEach(reference -> {
      if (reference instanceof HospitalService service) {
        recommendation.setService(service);

        List<Recommendation> recommendations = service
            .getRecommendations();
        recommendations.add(recommendation);
        service.setRecommendations(recommendations);
      } else if (reference instanceof Integer age) {
        recommendation.setAgeFrom(age - 5);
        recommendation.setAgeTo(age + 5);
      } else if (reference instanceof Gender gender) {
        recommendation.setLegalGender(gender);
      } else {
        throw new WrongClassException(
            "You can't pass this class as a parameter to the Recommendation Provider",
            reference,
            "reference");
      }
    });

    return recommendationRepository.save(recommendation);
  }
}
