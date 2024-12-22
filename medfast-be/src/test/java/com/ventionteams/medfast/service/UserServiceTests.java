package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.entity.Address;
import com.ventionteams.medfast.entity.ContactInfo;
import com.ventionteams.medfast.entity.Doctor;
import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.Specialization;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.Gender;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.exception.auth.TermsAndConditionsNotAcceptedException;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import com.ventionteams.medfast.exception.specialization.SpecializationsNotFoundException;
import com.ventionteams.medfast.repository.AddressRepository;
import com.ventionteams.medfast.repository.ContactInfoRepository;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.repository.PatientRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Checks user service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @Mock
  private UserRepository repository;

  @Mock
  private LocationService locationService;

  @Mock
  private SpecializationService specializationService;

  @Mock
  private DoctorRepository doctorRepository;

  @Mock
  private PatientRepository patientRepository;

  @Mock
  private AddressRepository addressRepository;

  @Mock
  private ContactInfoRepository contactInfoRepository;

  @Mock
  private AppConfig appConfig;

  @InjectMocks
  private UserService userService;

  private static DoctorRegistrationRequest doctorRequest;
  private static String encodedPwd;

  @BeforeAll
  static void setUp() {
    doctorRequest = DoctorRegistrationRequest.builder()
        .email("doctor@example.com")
        .locationId(1L)
        .specializationIds(List.of(1L, 2L))
        .licenseNumber("1234567")
        .birthDate(LocalDate.of(1980, 1, 1))
        .name("Anton")
        .surname("Dybko")
        .phone("12345678901")
        .build();
    encodedPwd = "encodedPassword";
  }

  @Test
  void createDoctor_CorrectDoctorRegistrationRequest_Success() {
    when(repository.existsByEmail(doctorRequest.getEmail())).thenReturn(false);
    when(locationService.findById(doctorRequest.getLocationId()))
        .thenReturn(new Location());
    when(specializationService.findAllById(doctorRequest.getSpecializationIds()))
        .thenReturn(List.of(
            new Specialization(), new Specialization()
        ));
    when(doctorRepository.save(any())).thenReturn(new Doctor());
    when(repository.save(any())).thenReturn(new User());
    when(appConfig.temporaryPasswordValidityDays()).thenReturn(1);

    userService.createDoctor(doctorRequest, encodedPwd);

    verify(doctorRepository).save(any());
    verify(repository).save(any());
    verify(repository).existsByEmail(doctorRequest.getEmail());
    verify(locationService).findById(doctorRequest.getLocationId());
    verify(specializationService).findAllById(doctorRequest.getSpecializationIds());
  }

  @Test
  void createDoctor_EmailAlreadyExists_ExceptionThrown() {
    when(repository.existsByEmail(doctorRequest.getEmail())).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class, () ->
        userService.createDoctor(doctorRequest, encodedPwd));
  }

  @Test
  void createDoctor_SpecializationsNotFound_ExceptionThrown() {
    Location location = new Location();
    when(repository.existsByEmail(doctorRequest.getEmail())).thenReturn(false);
    when(locationService.findById(doctorRequest.getLocationId()))
        .thenReturn(location);
    when(specializationService.findAllById(doctorRequest.getSpecializationIds()))
        .thenReturn(Collections.singletonList(new Specialization()));
    when(specializationService.getListOfMissingSpecializations(
        anyList(),
        anyList()
    )).thenThrow(SpecializationsNotFoundException.class);

    assertThrows(SpecializationsNotFoundException.class, () ->
        userService.createDoctor(doctorRequest, encodedPwd));
  }

  @Test
  public void getUserByEmail_InvalidEmail_ExceptionThrown() {
    String email = "invalid@example.com";

    when(repository.findByEmail(email)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> userService.findByEmail(email));
  }

  @Test
  public void getUserByEmail_ValidEmail_ReturnsUser() {
    User user = User.builder().id(3L).build();
    String email = "valid@example.com";

    when(repository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

    User returnedUser = userService.findByEmail(email);

    assertEquals(returnedUser, user);
  }

  @Test
  public void create_UserExist_ExceptionThrown() {
    PatientRegistrationRequest request = mock(PatientRegistrationRequest.class);

    when(repository.existsByEmail(request.getEmail())).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class,
        () -> userService.create(request));
  }

  @Test
  public void create_TermsAndConditionsNotAccepted_ExceptionThrown() {
    PatientRegistrationRequest request = mock(PatientRegistrationRequest.class);

    when(request.getCheckboxTermsAndConditions()).thenReturn(false);

    assertThrows(TermsAndConditionsNotAcceptedException.class,
        () -> userService.create(request));
  }

  @Test
  public void create_UserNotExist_CreateAndReturnUser() {
    PatientRegistrationRequest request = PatientRegistrationRequest.builder()
        .email("user@example.com")
        .password("passwrod")
        .name("John")
        .surname("Doe")
        .birthDate(LocalDate.now())
        .streetAddress("Main street")
        .house("123")
        .apartment("42 a")
        .city("Chicago")
        .citizenship("Illinios")
        .zip("60007")
        .phone("12345678900")
        .sex(Gender.MALE)
        .state("Canada")
        .checkboxTermsAndConditions(true)
        .build();

    when(repository.existsByEmail(request.getEmail())).thenReturn(false);

    when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(patientRepository.save(any(Patient.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
    when(addressRepository.save(any(Address.class))).thenAnswer(
        invocation -> invocation.getArgument(0));
    when(contactInfoRepository.save(any(ContactInfo.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    User createdUser = userService.create(request);

    assertNotNull(createdUser);
    assertEquals(request.getEmail(), createdUser.getEmail());
    assertEquals(request.getPassword(), createdUser.getPassword());
    assertEquals(Role.PATIENT, createdUser.getRole());
    Assertions.assertFalse(createdUser.isEnabled());

    Person createdPerson = createdUser.getPerson();
    assertNotNull(createdPerson);
    assertEquals(request.getName(), createdPerson.getName());
    assertEquals(request.getSurname(), createdPerson.getSurname());
    assertEquals(request.getBirthDate(), createdPerson.getBirthDate());
  }

  @Test
  public void resetPassword_ValidUser_UpdateUserPassword() {
    Person person = Person.builder()
        .build();
    User user = User.builder().email("user@example.com").person(person)
        .checkboxTermsAndConditions(true).build();
    String encodedPassword = "encodedPassword";

    when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    userService.resetPassword(user, encodedPassword);

    assertEquals(encodedPassword, user.getPassword());
    verify(repository, times(1)).save(user);
  }
}
