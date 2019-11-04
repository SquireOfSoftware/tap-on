package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.Service;
import org.springframework.data.repository.CrudRepository;

public interface JpaService extends CrudRepository<Service, Integer> {
}
