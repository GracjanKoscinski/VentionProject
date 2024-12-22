package com.ventionteams.medfast.service;

import static com.ventionteams.medfast.enums.UserStatus.ACTIVE;
import static com.ventionteams.medfast.enums.UserStatus.WAITING_FOR_CONFIRMATION;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.dto.request.DoctorRegistrationRequest;
import com.ventionteams.medfast.dto.request.PatientRegistrationRequest;
import com.ventionteams.medfast.dto.request.userinfo.AddressInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.ContactInfoRequest;
import com.ventionteams.medfast.dto.request.userinfo.PersonalInfoRequest;
import com.ventionteams.medfast.dto.response.userinfo.AddressInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.ContactInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.PersonalInfoResponse;
import com.ventionteams.medfast.dto.response.userinfo.UserInfoResponse;
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
import com.ventionteams.medfast.exception.userdetails.UpdatingNotExistingFieldsException;
import com.ventionteams.medfast.repository.AddressRepository;
import com.ventionteams.medfast.repository.ContactInfoRepository;
import com.ventionteams.medfast.repository.DoctorRepository;
import com.ventionteams.medfast.repository.PatientRepository;
import com.ventionteams.medfast.repository.PersonRepository;
import com.ventionteams.medfast.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for the User entity.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

  private final AppConfig appConfig;
  private final UserRepository repository;
  private final DoctorRepository doctorRepository;
  private final PatientRepository patientRepository;
  private final LocationService locationService;
  private final SpecializationService specializationService;
  private final AddressRepository addressRepository;
  private final ContactInfoRepository contactInfoRepository;
  private final PersonRepository personRepository;

  public UserDetailsService getUserDetailsService() {
    return this::findByEmail;
  }

  /**
   * Method seeks user by email.
   */
  public User findByEmail(String email) {
    return repository.findByEmail(email)
        .orElseThrow(() ->
            new UsernameNotFoundException("User with email " + email + " not found."));
  }

  /**
   * Creates a new user based on a sign-up request.
   */
  @Transactional
  public User create(PatientRegistrationRequest request) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException(request.getEmail());
    }

    if (!request.getCheckboxTermsAndConditions()) {
      throw new TermsAndConditionsNotAcceptedException(request.getEmail());
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(request.getPassword())
        .role(Role.PATIENT)
        .enabled(false)
        .userStatus(WAITING_FOR_CONFIRMATION)
        .checkboxTermsAndConditions(request.getCheckboxTermsAndConditions())
        .build();

    Address address = Address.builder()
        .streetAddress(request.getStreetAddress())
        .house(request.getHouse())
        .apartment(request.getApartment())
        .city(request.getCity())
        .state(request.getState())
        .zip(request.getZip())
        .build();
    addressRepository.save(address);

    ContactInfo contactInfo = ContactInfo.builder()
        .phone(request.getPhone())
        .build();
    contactInfoRepository.save(contactInfo);

    Patient patient = patientRepository.save(Patient.builder()
        .birthDate(request.getBirthDate())
        .name(request.getName())
        .surname(request.getSurname())
        .address(address)
        .contactInfo(contactInfo)
        .sex(request.getSex())
        .citizenship(request.getCitizenship())
        .user(user)
        .build());

    user.setPerson(patient);

    log.info("Attempt to create a user for {}", user.getEmail());
    return save(user);
  }

  /**
   * Creates a new doctor.
   */
  @Transactional
  public User createDoctor(DoctorRegistrationRequest request, String encodedPwd) {
    if (repository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException(request.getEmail());
    }

    Location location = locationService.findById(request.getLocationId());
    List<Specialization> specializationList = specializationService
        .findAllById(request.getSpecializationIds());

    List<Long> missingSpecializationIds = specializationService.getListOfMissingSpecializations(
        specializationList, request.getSpecializationIds()
    );
    if (!missingSpecializationIds.isEmpty()) {
      throw new SpecializationsNotFoundException(missingSpecializationIds);
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(encodedPwd)
        .role(Role.DOCTOR)
        .enabled(true)
        .userStatus(ACTIVE)
        .checkboxTermsAndConditions(false)
        .passwordExpirationDate(LocalDateTime.now().plusDays(
            appConfig.temporaryPasswordValidityDays()))
        .build();

    ContactInfo contactInfo = ContactInfo.builder()
        .phone(request.getPhone())
        .build();
    contactInfoRepository.save(contactInfo);

    Doctor doctor = doctorRepository.save(Doctor.builder()
        .licenseNumber(request.getLicenseNumber())
        .location(location)
        .specializations(specializationList)
        .birthDate(request.getBirthDate())
        .name(request.getName())
        .surname(request.getSurname())
        .sex(Gender.NEUTRAL)
        .contactInfo(contactInfo)
        .user(user)
        .build());

    user.setPerson(doctor);

    log.info("Attempt to create a doctor for {}", user.getEmail());
    return save(user);
  }

  /**
   * Resets the password for the user.
   */
  @Transactional
  public void resetPassword(User user, String encodedPassword) {
    if (!user.isCheckboxTermsAndConditions()) {
      throw new TermsAndConditionsNotAcceptedException(user.getEmail());
    }
    user.setPassword(encodedPassword);
    log.info("Attempt to reset password for the user with id {}", user.getId());
    save(user);
  }

  /**
   * Provides User's address info.
   */
  public AddressInfoResponse getAddressInfo(User user) {
    Address address = user.getPerson().getAddress();
    if (address == null) {
      return null;
    }
    return AddressInfoResponse.builder()
        .streetAddress(address.getStreetAddress())
        .house(address.getHouse())
        .apartment(address.getApartment())
        .city(address.getCity())
        .state(address.getState())
        .zip(address.getZip())
        .build();
  }

  /**
   * Provides User's contact info.
   */
  public ContactInfoResponse getContactInfo(User user) {
    ContactInfo contactInfo = user.getPerson().getContactInfo();
    return ContactInfoResponse.builder()
        .phone(contactInfo.getPhone())
        .email(user.getEmail())
        .build();
  }

  /**
   * Provides User's personal info.
   */
  public PersonalInfoResponse getPersonalInfo(User user) {
    Person person = user.getPerson();
    return PersonalInfoResponse.builder()
        .name(person.getName())
        .surname(person.getSurname())
        .birthDate(person.getBirthDate())
        .citizenship(person.getCitizenship())
        .sex(person.getSex())
        .build();
  }

  /**
   * Provides User's info.
   */
  public UserInfoResponse getUserInfo(User user) {
    AddressInfoResponse addressInfo = getAddressInfo(user);
    ContactInfoResponse contactInfo = getContactInfo(user);
    PersonalInfoResponse personalInfo = getPersonalInfo(user);
    return UserInfoResponse.builder()
        .checkboxTermsAndConditions(user.isCheckboxTermsAndConditions())
        .personalInfo(personalInfo)
        .addressInfo(addressInfo)
        .contactInfo(contactInfo)
        .build();
  }

  /**
   * Updates User's personal info.
   */
  @Transactional
  public PersonalInfoResponse updatePersonalInfo(User user, PersonalInfoRequest request) {
    Person person = user.getPerson();
    person.setName(request.getName());
    person.setSurname(request.getSurname());
    person.setBirthDate(request.getBirthDate());
    person.setCitizenship(request.getCitizenship());
    person.setSex(request.getSex());
    log.info("Attempt to update personal info for the user with id {}", user.getId());
    personRepository.save(person);
    return getPersonalInfo(user);
  }

  /**
   * Updates User's address info.
   */
  @Transactional
  public AddressInfoResponse updateAddressInfo(User user, AddressInfoRequest request) {
    Address address = user.getPerson().getAddress();
    if (address == null) {
      throw new UpdatingNotExistingFieldsException();
    }
    address.setStreetAddress(request.getStreetAddress());
    address.setHouse(request.getHouse());
    address.setApartment(request.getApartment());
    address.setCity(request.getCity());
    address.setState(request.getState());
    address.setZip(request.getZip());
    log.info("Attempt to update address info for the user with id {}", user.getId());
    addressRepository.save(address);
    return getAddressInfo(user);
  }

  /**
   * Updates User's contact info for not verified users.
   */
  @Transactional
  public ContactInfoResponse updateContactInfo(User user, ContactInfoRequest request) {
    ContactInfo userContactInfo = user.getPerson().getContactInfo();
    userContactInfo.setPhone(request.getPhone());
    log.info("Attempt to phone number for the user with id {}", user.getId());
    save(userContactInfo);
    return getContactInfo(user);
  }

  public User save(User user) {
    return repository.save(user);
  }

  public ContactInfo save(ContactInfo contactInfo) {
    return contactInfoRepository.save(contactInfo);
  }
}
