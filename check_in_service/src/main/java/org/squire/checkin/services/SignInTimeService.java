package org.squire.checkin.services;

import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.SignOutObject;

import java.sql.Timestamp;
import java.util.List;

public interface SignInTimeService {
    List<SignInDAO> getLatestSignIns();
    MessageObject addSignIns(List<SignInObject> signIns);
    MessageObject removeSignIns(List<SignOutObject> signOuts);
}
