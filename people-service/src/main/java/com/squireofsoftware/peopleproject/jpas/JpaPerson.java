package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaPerson extends CrudRepository<Person, Integer> {
    Optional<Person> findByHash(String hash);
}
