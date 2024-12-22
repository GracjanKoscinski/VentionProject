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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Service entity class.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "services", schema = "public")
public class HospitalService extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "service", nullable = false, unique = true)
  private String service;

  @Column(name = "duration", nullable = false)
  private Long duration;

  @EqualsAndHashCode.Exclude
  @ManyToMany(mappedBy = "services")
  private List<Specialization> specializations;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "service")
  private List<ConsultationAppointment> consultationAppointments;

  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "service")
  private List<Recommendation> recommendations;
}
