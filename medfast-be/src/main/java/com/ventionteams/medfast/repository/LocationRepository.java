package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the location entity.
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

  List<Location> findByDoctorsSpecializationsServicesAndDoctorsId(
      HospitalService service, Long doctorId);

  List<Location> findAllByTestsIdOrderByHospitalNameAsc(Long testId);

  Optional<Location> findByIdAndTestsId(Long id, Long testId);
}
