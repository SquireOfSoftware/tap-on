package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.NamePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNamePart extends JpaRepository<NamePart, Integer> {
    List<NamePart> findByPersonId(Integer personId);
    void deleteAllByPersonId(Integer personId);
}
