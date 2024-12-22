package com.ventionteams.medfast.specification;

import com.ventionteams.medfast.entity.Doctor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class to handle dynamic query specifications for Doctor entity.
 */
public class DoctorSpecification {

  /**
   * Filter by surname, name, or email.
   *
   * @param keyword the keyword to search for
   * @return the specification
   */
  public static Specification<Doctor> hasSurnameOrNameOrEmail(String keyword) {
    return (root, query, builder) -> {
      String likePattern = "%" + keyword.toLowerCase() + "%";
      return builder.or(
          builder.like(builder.lower(root.get("surname")), likePattern),
          builder.like(builder.lower(root.get("name")), likePattern),
          builder.like(builder.lower(root.get("user").get("email")), likePattern)
      );
    };
  }

  /**
   * Filter by user status.
   *
   * @param status the status to search for
   * @return the specification
   */
  public static Specification<Doctor> hasStatus(String status) {
    return (root, query, builder) ->
        builder.like(builder.lower(root.get("user").get("userStatus").as(String.class)),
            "%" + status.toLowerCase() + "%");
  }

  /**
   * Filter by specialization.
   *
   * @param specialization the specialization to search for
   * @return the specification
   */
  public static Specification<Doctor> hasSpecialization(String specialization) {
    return (root, query, builder) ->
        builder.like(builder.lower(root.get("specializations")
                .get("specialization").as(String.class)),
            "%" + specialization.toLowerCase() + "%");
  }
}
