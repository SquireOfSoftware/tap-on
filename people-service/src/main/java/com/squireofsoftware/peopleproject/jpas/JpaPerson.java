package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.Person;
import org.springframework.data.repository.CrudRepository;

public interface JpaPerson extends CrudRepository<Person, Integer> {
}
