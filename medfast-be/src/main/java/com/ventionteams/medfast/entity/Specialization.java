package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialization entity class.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "specializations", schema = "public")
public class Specialization extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "specialization", nullable = false, unique = true)
  private String specialization;

  @ManyToMany(mappedBy = "specializations")
  List<Doctor> doctors;

  @ManyToMany
  @JoinTable(
      name = "specializations_services_bridge",
      joinColumns = @JoinColumn(name = "specialization_id"),
      inverseJoinColumns = @JoinColumn(name = "service_id"))
  private List<HospitalService> services;
}
