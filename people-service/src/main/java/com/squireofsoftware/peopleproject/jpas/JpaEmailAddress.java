package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.EmailAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaEmailAddress extends CrudRepository<EmailAddress, Integer> {
    List<EmailAddress> findByPersonId(Integer personId);
}
