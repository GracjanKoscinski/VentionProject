package com.ventionteams.medfast.service;

import static com.ventionteams.medfast.enums.AppointmentStatus.CANCELLED_CLINIC;

import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorListResponse;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorResponse;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.DoctorFilterByRequest;
import com.ventionteams.medfast.enums.DoctorSortByRequest;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.mapper.doctor.DoctorToResponseMapper;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.service.appointment.ConsultationAppointmentService;
import com.ventionteams.medfast.service.password.OneTimePasswordService;
import com.ventionteams.medfast.service.password.PasswordService;
import com.ventionteams.medfast.specification.DoctorSpecification;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service which is accessible only by admin.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {

  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final PasswordService passwordService;
  private final OneTimePasswordService oneTimePasswordService;
  private final DoctorRepository doctorRepository;
  private final DoctorToResponseMapper doctorsToResponseMapper;
  private final ConsultationAppointmentService appointmentService;

  /**
   * Doctor registration.
   */
  @Transactional(rollbackFor = {MessagingException.class})
  public void registerDoctor(DoctorRegistrationRequest request) throws MessagingException {
    String pwd = passwordService.generatePassword();
    User doctorUser = userService.createDoctor(request, passwordEncoder.encode(pwd));
    oneTimePasswordService.sendTemporaryPasswordEmail(request.getEmail(), pwd);
    log.info("Accepted doctor registration request for doctor with email {}",
        doctorUser.getEmail());
  }

  /**
   * Get all doctors sorted by given field.
   */
  public DoctorListResponse getDoctors(Optional<Integer> page,
      Optional<Integer> amount,
      Optional<DoctorSortByRequest> sortBy) {

    DoctorSortByRequest sort = sortBy.orElse(DoctorSortByRequest.SURNAME);
    int pageSize = amount.orElse(10);
    int pageNumber = page.orElse(0);
    Pageable pageable = PageRequest.of(pageNumber, pageSize);

    List<Doctor> doctors = switch (sort) {
      case EMAIL -> doctorRepository.findAllByOrderByUserEmailAsc(pageable);
      case STATUS -> doctorRepository.findAllByOrderByUserUserStatusAsc(pageable);
      case SPECIALIZATION ->
          doctorRepository.findAllByOrderBySpecializationsSpecializationAsc(pageable);
      default -> doctorRepository.findAllByOrderBySurnameAsc(pageable);
    };
    List<DoctorResponse> doctorsResponse = doctorsToResponseMapper.apply(doctors);

    return DoctorListResponse.builder()
        .doctors(doctorsResponse)
        .totalAmount(doctorRepository.findAll().size())
        .build();
  }

  /**
   * Search doctors by given keyword and field.
   */
  public DoctorListResponse searchDoctors(Optional<Integer> page,
      Optional<Integer> amount,
      Optional<DoctorSortByRequest> sortBy,
      Optional<DoctorFilterByRequest> filterBy,
      String keyword) {

    int pageSize = amount.orElse(10);
    int pageNumber = page.orElse(0);
    DoctorFilterByRequest filter = filterBy.orElse(DoctorFilterByRequest.SURNAME);
    DoctorSortByRequest sort = sortBy.orElse(DoctorSortByRequest.SURNAME);

    Specification<Doctor> spec = Specification.where(null);

    spec = switch (filter) {
      case SURNAME, NAME, EMAIL -> spec.and(DoctorSpecification.hasSurnameOrNameOrEmail(keyword));
      case STATUS -> spec.and(DoctorSpecification.hasStatus(keyword));
      case SPECIALIZATION -> spec.and(DoctorSpecification.hasSpecialization(keyword));
    };

    Sort sortCriteria;
    if (sort.equals(DoctorSortByRequest.SPECIALIZATION)) {
      sortCriteria = Sort.by(Sort.Order.asc("specializations.specialization"));
    } else if (sort.equals(DoctorSortByRequest.STATUS)) {
      sortCriteria = Sort.by(Sort.Order.asc("user.userStatus"));
    } else if (sort.equals(DoctorSortByRequest.EMAIL)) {
      sortCriteria = Sort.by(Sort.Order.asc("user.email"));
    } else {
      sortCriteria = Sort.by(Sort.Order.asc("surname"));
    }

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sortCriteria);

    List<DoctorResponse> doctors = doctorRepository.findAll(spec, pageable)
        .stream()
        .map(doctorsToResponseMapper::doctorToResponse)
        .toList();

    return DoctorListResponse.builder()
        .doctors(doctors)
        .totalAmount((int) doctorRepository.count(spec))
        .build();
  }


  /**
   * Deactivates user.
   */
  public void deactivateUser(String email, UserStatus status) {
    User user = userService.findByEmail(email);
    user.setUserStatus(status);
    userService.save(user);
    log.info(String.valueOf(status), " user with email {}", email);
    Person person = user.getPerson();
    if (person instanceof Doctor doctor) {
      doctor.getConsultationAppointments().forEach(consultationAppointment -> {
        appointmentService.changeStatus(consultationAppointment, CANCELLED_CLINIC);
      });
    }
  }

  /**
   * Activates user.
   */
  public void activateUser(String email) {
    User user = userService.findByEmail(email);
    if (user.isEnabled()) {
      user.setUserStatus(UserStatus.ACTIVE);
    } else {
      user.setUserStatus(UserStatus.WAITING_FOR_CONFIRMATION);
    }
    userService.save(user);
  }
}