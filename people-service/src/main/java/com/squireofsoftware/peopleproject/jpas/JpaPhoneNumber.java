package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPhoneNumber extends JpaRepository<PhoneNumber, Integer> {
    List<PhoneNumber> findByPersonId(Integer personId);
    void deleteAllByPersonId(Integer personId);
}
