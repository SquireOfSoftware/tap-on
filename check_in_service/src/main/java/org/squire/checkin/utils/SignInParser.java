package org.squire.checkin.utils;

import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.SignInObject;

import java.util.List;
import java.util.stream.Collectors;

public class SignInParser {
    public static final String TIME_COLUMN_NAME = "signInTime";
    public static List<SignInObject> parseSignInDAOs(List<SignInDAO> signInDAOList) {
        return signInDAOList.stream().map(SignInParser::parseSignInDAO).collect(Collectors.toList());
    }

    public static SignInObject parseSignInDAO(SignInDAO signInDAO) {
        SignInObject signInObject = new SignInObject();
        signInObject.setPersonId(signInDAO.getPersonId());
        signInObject.setSignInTime(signInDAO.getSignInTime());
        return signInObject;
    }

    public static SignInDAO parseSignInObject(SignInObject signInObject) {
        SignInDAO signInDAO = new SignInDAO();
        signInDAO.setPersonId(signInObject.getPersonId());
        signInDAO.setSignInTime(signInObject.getSignInTime());
        return signInDAO;
    }
}
