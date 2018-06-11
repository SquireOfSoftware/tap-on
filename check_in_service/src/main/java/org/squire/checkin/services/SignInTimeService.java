package org.squire.checkin.services;

import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.SignInObject;

import java.util.List;

public interface SignInTimeService {
    List<SignInDAO> getLatestSignIns();
    boolean addSignIns(List<SignInObject> signIns);
}
