package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;

import java.util.List;

public interface CheckinLogService {
    CheckinLogObject checkin(SignInObject signInLogObject);

    List<CheckinLogObject> getLogs(Integer hash);
}
