package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckinLogService {
    CheckinLogObject checkin(SignInObject signInLogObject);

    List<CheckinLogObject> getAllLogs(Integer hash);

    List<CheckinLogObject> getLogsFromTo(Integer hash, LocalDateTime from, LocalDateTime to);

    List<CheckinLogObject> getLogsFrom(Integer hash, LocalDateTime from);

    List<CheckinLogObject> getLogsTo(Integer hash, LocalDateTime to);
}
