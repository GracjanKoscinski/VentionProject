package com.ventionteams.medfast.entity.base;

import com.ventionteams.medfast.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Base token with common fields for all token entities.
 */
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class TokenEntity extends BaseEntity {

  @Column(name = "token", nullable = false)
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
