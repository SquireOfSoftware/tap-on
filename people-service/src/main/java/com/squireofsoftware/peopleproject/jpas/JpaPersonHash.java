package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.PersonHash;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaPersonHash extends CrudRepository<PersonHash, Integer> {
    Optional<PersonHash> findByHash(Integer hash);
    Optional<PersonHash> findByPersonId(Integer personId);
}
