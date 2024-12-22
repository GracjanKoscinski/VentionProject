package com.ventionteams.medfast.pdf;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ventionteams.medfast.entity.Location;
import com.ventionteams.medfast.entity.MedicalTest;
import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.entity.Patient;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.enums.AppointmentStatus;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests the test result PDF generator functionality with unit test.
 */
public class TestAppointmentPdfGeneratorTest {

  @Test
  void generatePdf_ValidInput_ReturnsByteArray() {
    Patient patient = new Patient();
    patient.setSurname("Doe");
    patient.setName("John");
    patient.setSurname("Doe");
    LocalDate dateOfBirth = LocalDate.of(1980, 1, 1);
    patient.setBirthDate(dateOfBirth);
    User user = new User();
    user.setPerson(patient);
    MedicalTest test = new MedicalTest();
    test.setTest("test");
    Location location = Location.builder().house("35")
        .hospitalName("Test Hospital").streetAddress("Test street").build();
    LocalDateTime testDateTime = LocalDateTime.now();
    MedicalTestAppointment testAppointment = MedicalTestAppointment.builder()
        .dateTime(testDateTime)
        .status(AppointmentStatus.SCHEDULED)
        .test(test)
        .patient(patient)
        .location(location)
        .build();
    MedicalTestAppointmentRepository medicalTestAppointmentRepository
        = Mockito.mock(MedicalTestAppointmentRepository.class);
    TestAppointmentPdfGenerator testAppointmentPdfGenerator =
        new TestAppointmentPdfGenerator(medicalTestAppointmentRepository);

    testAppointmentPdfGenerator.generateAndSaveTestPdf(testAppointment);
    assertNotNull(testAppointment.getPdf(), "Generated PDF byte array should not be null");
    assertTrue(testAppointment.getPdf().length > 0, "Generated PDF byte array should not be empty");
  }

}
