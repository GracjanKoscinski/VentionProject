package com.ventionteams.medfast.repository;

import com.ventionteams.medfast.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the person entity.
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

}
