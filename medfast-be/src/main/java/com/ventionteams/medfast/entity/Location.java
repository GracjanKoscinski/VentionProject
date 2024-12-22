package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Location entity class.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "locations", schema = "public")
public class Location extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hospital_name", nullable = false)
  private String hospitalName;

  @Column(name = "street_address", nullable = false)
  private String streetAddress;

  @Column(name = "house", nullable = false)
  private String house;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "location")
  private List<ConsultationAppointment> consultationAppointments;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "location")
  private List<Doctor> doctors;

  @EqualsAndHashCode.Exclude
  @ManyToMany(mappedBy = "locations")
  private List<MedicalTest> tests;

  @Override
  public String toString() {
    return hospitalName + ", " + house + " " + streetAddress;
  }
}
