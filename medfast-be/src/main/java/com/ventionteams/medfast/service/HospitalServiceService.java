package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.HospitalService;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.HospitalServiceRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service class for the appointment entity.
 */
@Service
@RequiredArgsConstructor
public class HospitalServiceService {

  private final HospitalServiceRepository serviceRepository;

  /**
   * Returns list of all available hospital servicesas a List of HospitalServiceResponses.
   */
  public List<HospitalService> getAllServices() {
    return serviceRepository.findAll();
  }

  public HospitalService findById(Long serviceId) {
    return serviceRepository.findById(serviceId).orElseThrow(
        () -> new EntityNotFoundException(HospitalService.class, serviceId));
  }

  /**
   * Returns list of recommendations depending on patient's age and gender.
   */
  public List<HospitalService> getRecommendedServices(
      User authenticatedUser,
      ElementSelection selection) {
    Patient authenticatedPatient = (Patient) authenticatedUser.getPerson();
    int age = Period.between(authenticatedPatient.getBirthDate(), LocalDate.now()).getYears();

    Pageable pageable;
    List<HospitalService> recommendedServices;
    if (selection == ElementSelection.FIRST) {
      pageable = PageRequest.of(0, 2, Sort.by("service.service").ascending());
      recommendedServices = serviceRepository
          .findServicesByAgeAndLegalGender(
              age, authenticatedPatient.getSex(), pageable);
    } else {
      pageable = PageRequest.of(0, 1000, Sort.by("service.service").ascending());
      recommendedServices = serviceRepository
          .findServicesByAgeAndLegalGender(
              age, authenticatedPatient.getSex(), pageable).stream().skip(2).toList();
    }

    return recommendedServices;
  }
}
