package com.ventionteams.medfast.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.AppConfig;
import com.ventionteams.medfast.entity.ConsultationAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.Referral;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentRequestType;
import com.ventionteams.medfast.enums.ElementSelection;
import com.ventionteams.medfast.exception.EntityNotFoundException;
import com.ventionteams.medfast.repository.ReferralRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Checks referral service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class ReferralServiceTests {

  @Mock
  private ReferralRepository referralRepository;
  @Mock
  private AppConfig appConfig;

  @InjectMocks
  private ReferralService referralService;

  private static User authenticatedUser;
  private static Patient authenticatedPatient;
  private static Pageable firstPageable;
  private static Pageable remainingPageable;
  private static List<Referral> allReferrals;
  private static List<Referral> firstReferrals;

  @BeforeAll
  static void setUp() {
    authenticatedPatient = Patient.builder().build();
    authenticatedUser = User.builder().person(authenticatedPatient).build();

    firstPageable = PageRequest.of(0, 3, Sort.by("dateOfIssue").descending());
    remainingPageable = PageRequest.of(0, 1000, Sort.by("dateOfIssue").descending());
    firstReferrals = List.of(new Referral(), new Referral(), new Referral());
    allReferrals = List.of(new Referral(), new Referral(), new Referral(), new Referral());
  }


  @Test
  void getReferrals_FirstSelection_UpcomingReferrals() {
    when(appConfig.elementCountLimit()).thenReturn(3);
    when(referralRepository.findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
        eq(authenticatedPatient), eq(LocalDate.now()), eq(firstPageable)))
        .thenReturn(firstReferrals);

    List<Referral> result = referralService.getReferrals(authenticatedUser, ElementSelection.FIRST,
        AppointmentRequestType.UPCOMING);

    assertEquals(3, result.size());
    verify(referralRepository).findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
        authenticatedPatient, LocalDate.now(), firstPageable);
  }

  @Test
  void getReferrals_FirstSelection_ExpiredOrFulfilledReferrals() {
    when(appConfig.elementCountLimit()).thenReturn(3);
    when(referralRepository.findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
        eq(authenticatedPatient), eq(LocalDate.now()), eq(firstPageable)))
        .thenReturn(firstReferrals);

    List<Referral> result = referralService.getReferrals(authenticatedUser, ElementSelection.FIRST,
        AppointmentRequestType.PAST);

    assertEquals(3, result.size());
    verify(
        referralRepository).findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
        authenticatedPatient, LocalDate.now(), firstPageable);
  }

  @Test
  void getReferrals_RemainingSelection_UpcomingReferrals() {
    when(appConfig.elementCountLimit()).thenReturn(3);
    when(referralRepository.findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
        eq(authenticatedPatient), eq(LocalDate.now()), eq(remainingPageable)))
        .thenReturn(allReferrals);

    List<Referral> result = referralService.getReferrals(authenticatedUser,
        ElementSelection.REMAINING, AppointmentRequestType.UPCOMING);

    assertEquals(1, result.size());
    verify(referralRepository).findByPatientAndExpirationDateAfterAndConsultationAppointmentIsNull(
        authenticatedPatient, LocalDate.now(), remainingPageable);
  }

  @Test
  void getReferrals_RemainingSelection_ExpiredOrFulfilledReferrals() {
    when(appConfig.elementCountLimit()).thenReturn(3);
    when(referralRepository.findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
        eq(authenticatedPatient), eq(LocalDate.now()), eq(remainingPageable)))
        .thenReturn(allReferrals);

    List<Referral> result = referralService.getReferrals(authenticatedUser,
        ElementSelection.REMAINING, AppointmentRequestType.PAST);

    assertEquals(1, result.size());
    verify(
        referralRepository).findByPatientAndExpirationDateBeforeOrConsultationAppointmentIsNotNull(
        authenticatedPatient, LocalDate.now(), remainingPageable);
  }

  @Test
  void assignConsultationAppointmentToReferral() {
    Referral referral = new Referral();
    ConsultationAppointment appointment = new ConsultationAppointment();

    when(referralRepository.save(any(Referral.class))).thenReturn(referral);

    referralService.assignConsultationAppointmentToReferral(referral, appointment);

    assertEquals(appointment, referral.getConsultationAppointment(),
        "Consultation appointment should be assigned to referral");
    verify(referralRepository, times(1)).save(referral);
  }

  @Test
  void findById_Success() {
    Long referralId = 1L;
    Referral referral = new Referral();
    referral.setId(referralId);

    when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));

    Referral result = referralService.findById(referralId);

    assertEquals(referral, result, "Referral should be found by ID");
    verify(referralRepository, times(1)).findById(referralId);
  }

  @Test
  void findById_NotFound() {
    Long referralId = 1L;

    when(referralRepository.findById(referralId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> referralService.findById(referralId),
        "ReferralNotFoundException should be thrown when referral is not found");
    verify(referralRepository, times(1)).findById(referralId);
  }
}