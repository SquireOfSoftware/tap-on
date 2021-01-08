package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.CheckinLog;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaCheckinLog extends CrudRepository<CheckinLog, Integer> {
    List<CheckinLog> findAllByPersonId(Integer personId);
}
