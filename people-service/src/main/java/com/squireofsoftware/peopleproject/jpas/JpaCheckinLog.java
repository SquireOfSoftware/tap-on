package com.squireofsoftware.peopleproject.jpas;

import com.squireofsoftware.peopleproject.entities.CheckinLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface JpaCheckinLog extends JpaRepository<CheckinLog, Integer> {
    List<CheckinLog> findAllByPersonId(Integer personId);

    List<CheckinLog> findByPersonIdAndTimestampBetween(Integer personId, Timestamp to, Timestamp from);

    List<CheckinLog> findByPersonIdAndTimestampAfter(Integer personId, Timestamp from);

    List<CheckinLog> findByPersonIdAndTimestampBefore(Integer personId, Timestamp to);

    List<CheckinLog> findByTimestampAfter(Timestamp from);
}
