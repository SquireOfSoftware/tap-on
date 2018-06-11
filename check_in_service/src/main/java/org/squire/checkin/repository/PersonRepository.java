package org.squire.checkin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.squire.checkin.entities.PersonDAO;

import java.util.List;

public interface PersonRepository extends CrudRepository<PersonDAO, Integer>{
    @Query("SELECT p from PersonDAO p " +
            "WHERE p.memberSince IS NOT NULL")
    List<PersonDAO> findAllByMemberSinceIsNotNull();
}
