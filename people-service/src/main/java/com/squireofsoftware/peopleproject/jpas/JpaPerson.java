package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface JpaPerson extends JpaRepository<Person, Integer> {
    Optional<Person> findByHash(String hash);

    List<Person> findByCreationDateAfter(Timestamp timestamp, Sort sort);
}
