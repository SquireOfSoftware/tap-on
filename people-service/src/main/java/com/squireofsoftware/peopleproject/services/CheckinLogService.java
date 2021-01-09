package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckinLogService {
    CheckinLogObject checkin(SignInObject signInLogObject);

    List<CheckinLogObject> getAllPersonsLogs(Integer hash);

    List<CheckinLogObject> getPersonsLogsFromTo(Integer hash, LocalDateTime from, LocalDateTime to);

    List<CheckinLogObject> getPersonsLogsFrom(Integer hash, LocalDateTime from);

    List<CheckinLogObject> getPersonsLogsTo(Integer hash, LocalDateTime to);

    List<CheckinLogObject> getSignInsForToday();

    List<CheckinLogObject> getSignInsFrom(LocalDateTime from);
}
