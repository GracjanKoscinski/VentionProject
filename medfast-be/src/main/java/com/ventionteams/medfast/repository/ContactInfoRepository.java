package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the ContactInfo entity.
 */
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long>  {

}
