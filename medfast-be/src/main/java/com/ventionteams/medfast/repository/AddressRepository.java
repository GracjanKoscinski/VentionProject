package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the ContactInfo entity.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

}
