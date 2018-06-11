package org.squire.checkin.repository;


import org.springframework.data.repository.CrudRepository;
import org.squire.checkin.entities.Person;

public interface PersonRepository extends CrudRepository<Person, Integer>{
}
