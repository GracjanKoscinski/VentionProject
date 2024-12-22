package com.ventionteams.medfast.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Patient entity class.
 */
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "patients", schema = "public")
public class Patient extends Person {

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
  private List<ConsultationAppointment> consultationAppointments;

  @OneToOne
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
  private List<MedicalTestAppointment> medicalTestAppointments;
}
