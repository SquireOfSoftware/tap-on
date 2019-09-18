package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.NamePart;
import org.springframework.data.repository.CrudRepository;

public interface JpaNamePart extends CrudRepository<NamePart, Integer> {
}
