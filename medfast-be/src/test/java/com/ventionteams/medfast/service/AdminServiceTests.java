package com.ventionteams.medfast.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorListResponse;
import com.ventionteams.medfast.dto.response.adminconsole.DoctorResponse;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.enums.DoctorSortByRequest;
import com.ventionteams.medfast.enums.UserStatus;
import com.ventionteams.medfast.mapper.doctor.DoctorToResponseMapper;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.service.appointment.ConsultationAppointmentService;
import com.ventionteams.medfast.service.appointment.MedicalTestAppointmentService;
import com.ventionteams.medfast.service.password.OneTimePasswordService;
import com.ventionteams.medfast.service.password.PasswordService;
import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Checks admin service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class AdminServiceTests {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserService userService;

  @Mock
  private PasswordService passwordService;

  @Mock
  private DoctorRepository doctorRepository;

  @Mock
  private ConsultationAppointmentService appointmentService;

  @Mock
  private MedicalTestAppointmentService medicalTestAppointmentService;

  @Mock
  private DoctorToResponseMapper doctorToResponseMapper;

  @Mock
  private OneTimePasswordService oneTimePasswordService;

  @Mock
  private User mockUser;

  @Mock
  private Doctor mockDoctor;

  @Mock
  private Person mockPerson;

  @Mock
  private ConsultationAppointment mockConsultationAppointment;

  String email = "user@example.com";
  private static List<Doctor> doctors;
  private static List<DoctorResponse> responses;
  private static DoctorListResponse doctorListResponse;
  private static final Pageable pageable = PageRequest.of(0, 2);

  @InjectMocks
  private AdminService adminService;

  @BeforeAll
  static void setUp() {
    doctors = List.of(new Doctor(), new Doctor());
    responses = List.of(new DoctorResponse(), new DoctorResponse());
    doctorListResponse = DoctorListResponse.builder()
        .doctors(responses)
        .totalAmount(0)
        .build();
  }

  @Test
  void registerDoctor_SuccessfulRegistration_EmailSent() throws MessagingException {
    String generatedPassword = "generatedPassword";
    String encodedPassword = "encodedPassword";
    DoctorRegistrationRequest request = new DoctorRegistrationRequest();
    request.setEmail(email);

    when(passwordService.generatePassword()).thenReturn(generatedPassword);
    when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);
    when(userService.createDoctor(any(DoctorRegistrationRequest.class), eq(encodedPassword)))
        .thenReturn(new User());
    doNothing().when(oneTimePasswordService).sendTemporaryPasswordEmail(anyString(), anyString());

    adminService.registerDoctor(request);

    verify(passwordService).generatePassword();
    verify(userService)
        .createDoctor(any(DoctorRegistrationRequest.class), eq(encodedPassword));
    verify(oneTimePasswordService)
        .sendTemporaryPasswordEmail(eq(request.getEmail()), eq(generatedPassword));
  }

  @Test
  public void getDoctorsByEmail_ValidRequest_ReturnsList() {
    when(doctorRepository.findAllByOrderByUserEmailAsc(pageable)).thenReturn(doctors);
    when(doctorToResponseMapper.apply(doctors)).thenReturn(responses);

    DoctorListResponse result = adminService.getDoctors(Optional.of(0),
        Optional.of(2), Optional.of(DoctorSortByRequest.EMAIL));

    Assertions.assertEquals(doctorListResponse, result);
    verify(doctorRepository).findAllByOrderByUserEmailAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserUserStatusAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySpecializationsSpecializationAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySurnameAsc(pageable);
  }

  @Test
  public void getDoctorsByStatus_ValidRequest_ReturnsList() {
    when(doctorRepository.findAllByOrderByUserUserStatusAsc(pageable)).thenReturn(doctors);
    when(doctorToResponseMapper.apply(doctors)).thenReturn(responses);

    DoctorListResponse result = adminService.getDoctors(Optional.of(0),
        Optional.of(2), Optional.of(DoctorSortByRequest.STATUS));

    Assertions.assertEquals(doctorListResponse, result);
    verify(doctorRepository).findAllByOrderByUserUserStatusAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserEmailAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySpecializationsSpecializationAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySurnameAsc(pageable);
  }

  @Test
  public void getDoctorsBySpecialization_ValidRequest_ReturnsList() {
    when(doctorRepository
        .findAllByOrderBySpecializationsSpecializationAsc(pageable)).thenReturn(doctors);
    when(doctorToResponseMapper.apply(doctors)).thenReturn(responses);

    DoctorListResponse result = adminService.getDoctors(Optional.of(0),
        Optional.of(2), Optional.of(DoctorSortByRequest.SPECIALIZATION));

    Assertions.assertEquals(doctorListResponse, result);
    verify(doctorRepository).findAllByOrderBySpecializationsSpecializationAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserEmailAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserUserStatusAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySurnameAsc(pageable);
  }

  @Test
  public void getDoctorsDefaultSorting_ValidRequest_ReturnsList() {
    when(doctorRepository.findAllByOrderBySurnameAsc(pageable)).thenReturn(doctors);
    when(doctorToResponseMapper.apply(doctors)).thenReturn(responses);

    DoctorListResponse result = adminService.getDoctors(Optional.of(0),
        Optional.of(2), Optional.empty());

    Assertions.assertEquals(doctorListResponse, result);
    verify(doctorRepository).findAllByOrderBySurnameAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserEmailAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserUserStatusAsc(pageable);
    verify(doctorRepository,
        never()).findAllByOrderBySpecializationsSpecializationAsc(pageable);
  }

  @Test
  public void getDoctorsWithDefaultAmount_ValidRequest_ReturnsList() {
    when(doctorRepository.findAllByOrderBySurnameAsc(pageable)).thenReturn(doctors);
    when(doctorToResponseMapper.apply(doctors)).thenReturn(responses);

    DoctorListResponse result = adminService.getDoctors(Optional.empty(),
        Optional.of(2), Optional.empty());

    Assertions.assertEquals(doctorListResponse, result);
    verify(doctorRepository).findAllByOrderBySurnameAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserEmailAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderByUserUserStatusAsc(pageable);
    verify(doctorRepository, never()).findAllByOrderBySpecializationsSpecializationAsc(pageable);
  }

  @Test
  public void deactivateUser_DoctorUser() {
    when(userService.findByEmail(email)).thenReturn(mockUser);
    when(mockUser.getPerson()).thenReturn(mockDoctor);
    when(mockDoctor.getConsultationAppointments()).thenReturn(List.of(mockConsultationAppointment));

    adminService.deactivateUser(email, UserStatus.DEACTIVATED);

    verify(userService).findByEmail(email);
    verify(mockUser).setUserStatus(UserStatus.DEACTIVATED);
    verify(userService).save(mockUser);
    verify(appointmentService)
        .changeStatus(mockConsultationAppointment, AppointmentStatus.CANCELLED_CLINIC);
  }

  @Test
  public void deactivateUser_NonDoctorUser() {
    when(userService.findByEmail(email)).thenReturn(mockUser);
    when(mockUser.getPerson()).thenReturn(mockPerson);

    adminService.deactivateUser(email, UserStatus.DEACTIVATED);

    verify(userService).findByEmail(email);
    verify(mockUser).setUserStatus(UserStatus.DEACTIVATED);
    verify(userService).save(mockUser);
    verifyNoInteractions(appointmentService);
    verifyNoInteractions(medicalTestAppointmentService);
  }

  @Test
  public void activateUser_UserIsEnabled() {
    when(userService.findByEmail(email)).thenReturn(mockUser);
    when(mockUser.isEnabled()).thenReturn(true);

    adminService.activateUser(email);

    verify(mockUser).setUserStatus(UserStatus.ACTIVE);
    verify(userService).save(mockUser);
  }

  @Test
  public void activateUser_UserIsDisabled() {
    when(userService.findByEmail(email)).thenReturn(mockUser);
    when(mockUser.isEnabled()).thenReturn(false);

    adminService.activateUser(email);

    verify(mockUser).setUserStatus(UserStatus.WAITING_FOR_CONFIRMATION);
    verify(userService).save(mockUser);
  }
}


