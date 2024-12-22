package com.ventionteams.medfast.entity;

import com.ventionteams.medfast.entity.base.BaseEntity;
import com.ventionteams.medfast.enums.Role;
import com.ventionteams.medfast.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Application user entity class.
 */
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Table(name = "users", schema = "public")
public class User extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_status", nullable = false)
  private UserStatus userStatus;

  @Column(name = "checkbox_terms_and_conditions", nullable = false)
  private boolean checkboxTermsAndConditions;

  @EqualsAndHashCode.Exclude
  @OneToOne
  @JoinColumn(name = "person_id", nullable = false)
  private Person person;

  @Column(name = "password_expiration_date")
  private LocalDateTime passwordExpirationDate;

  @OneToOne(mappedBy = "user")
  private ProfilePictureMetadata profilePicture;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public boolean isEnabled() {
    return enabled == null || enabled;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    if (passwordExpirationDate != null) {
      return passwordExpirationDate.isAfter(LocalDateTime.now());
    } else {
      return true;
    }
  }
}
