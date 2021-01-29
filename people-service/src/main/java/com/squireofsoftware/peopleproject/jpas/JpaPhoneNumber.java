package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.PhoneNumber;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaPhoneNumber extends CrudRepository<PhoneNumber, Integer> {
    List<PhoneNumber> findByPersonId(Integer personId);
}
