package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPerson extends JpaRepository<Person, Integer> {
    Optional<Person> findByHash(String hash);
}
