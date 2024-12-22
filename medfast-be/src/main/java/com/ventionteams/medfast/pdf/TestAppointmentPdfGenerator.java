package com.ventionteams.medfast.pdf;

import com.ventionteams.medfast.entity.MedicalTestAppointment;
import com.ventionteams.medfast.pdf.base.BasePdfService;
import com.ventionteams.medfast.repository.MedicalTestAppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating and saving test result pdf.
 */
@Service
@RequiredArgsConstructor
public class TestAppointmentPdfGenerator extends BasePdfService {

  private final MedicalTestAppointmentRepository medicalTestAppointmentRepository;

  /**
   * Generates and saves a test result pdf for given test appointment.
   */
  @Transactional
  public void generateAndSaveTestPdf(MedicalTestAppointment medicalTestAppointment) {

    String patientName = medicalTestAppointment.getPatient().getName().concat(" ")
        .concat(medicalTestAppointment.getPatient().getSurname());
    String[] testInfo = {
        "Test Name: " + medicalTestAppointment.getTest().getTest(),
        "Test Status: " + medicalTestAppointment.getStatus().name(),
        "Test Location: " + medicalTestAppointment.getLocation().toString(),
        "Patient Name: " + patientName,
        "Date of Birth: " + medicalTestAppointment.getPatient()
            .getBirthDate().toString(),
        "Test Date: " + medicalTestAppointment.getDateTime().toString(),
    };
    String[] headers = {"Test", "Result", "Units", "Reference Range"};
    String[][] content = {
        {"Hemoglobin", getRandomValue(13.0, 17.0), "g/dL", "13.0 - 17.0"},
        {"WBC", getRandomInt(4500, 11000), "cells/mcL", "4,500 - 11,000"},
        {"Platelets", getRandomInt(150000, 450000), "/mcL", "150,000 - 450,000"},
        {"Blood Glucose", getRandomValue(70.0, 99.0), "mg/dL", "70 - 99"},
        {"RBC", getRandomValue(4.2, 6.0), "mil/mcL", "4.2 - 6.0"},
        {"Cholesterol", getRandomValue(150.0, 300.0), "mg/dL", "< 200"},
        {"Triglycerides", getRandomValue(50.0, 200.0), "mg/dL", "< 150"},
        {"LDL", getRandomValue(70.0, 180.0), "mg/dL", "< 100"},
        {"HDL", getRandomValue(40.0, 90.0), "mg/dL", "40 - 60"},
        {"Uric Acid", getRandomValue(3.0, 7.5), "mg/dL", "3.0 - 7.5"}
    };

    byte[] pdf = generatePdf(testInfo, "Medical Test Result", PDRectangle.A4);
    pdf = addTableToPdf(headers, content, 500, pdf);
    pdf = addDoctorSignatureToPdf(pdf, 200);
    medicalTestAppointment.setPdf(pdf);
    medicalTestAppointmentRepository.setPdfById(pdf, medicalTestAppointment.getId());
  }


}
