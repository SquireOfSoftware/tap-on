package org.squire.checkin.services;

import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.SignOutObject;

import java.util.List;

public interface SignInTimeService {
    SignInObject getLatestSignIn(Integer personId);
    MessageObject signInPersonId(Integer id);
    MessageObject addSignIns(List<SignInObject> signIns);
    MessageObject removeSignIns(List<SignOutObject> signOuts);
}
