package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.NamePart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaNamePart extends CrudRepository<NamePart, Integer> {
    List<NamePart> findByPersonId(Integer personId);
}
