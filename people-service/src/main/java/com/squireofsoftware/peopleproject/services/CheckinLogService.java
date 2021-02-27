package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckinLogService {
    CheckinLogObject checkin(SignInObject signInLogObject);

    List<CheckinLogObject> getAllPersonsLogs(String hash);

    List<CheckinLogObject> getPersonsLogsFromTo(String hash, LocalDateTime from, LocalDateTime to);

    List<CheckinLogObject> getPersonsLogsFrom(String hash, LocalDateTime from);

    List<CheckinLogObject> getPersonsLogsTo(String hash, LocalDateTime to);

    List<CheckinLogObject> getSignInsForToday();

    List<CheckinLogObject> getSignInsFrom(LocalDateTime from);
}
