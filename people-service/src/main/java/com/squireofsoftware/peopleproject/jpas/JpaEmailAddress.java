package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaEmailAddress extends JpaRepository<EmailAddress, Integer> {
    List<EmailAddress> findByPersonId(Integer personId);
    void deleteAllByPersonId(Integer personId);
}
