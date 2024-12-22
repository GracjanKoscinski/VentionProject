package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import com.ventionteams.medfast.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Person entity class.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "persons", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "surname", nullable = false)
  private String surname;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "sex", nullable = false)
  private Gender sex;

  @Column(name = "citizenship")
  private String citizenship;

  @OneToOne
  @JoinColumn(name = "contact_info_id", nullable = false)
  private ContactInfo contactInfo;

  @OneToOne(mappedBy = "person")
  private User user;

  /**
   * Returns address if it is a patient. Returns null if it is not a patient.
   */
  public Address getAddress() {
    if (this instanceof Patient) {
      return this.getAddress();
    } else {
      return null;
    }
  }
}
