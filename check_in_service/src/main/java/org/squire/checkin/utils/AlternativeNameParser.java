package org.squire.checkin.utils;

import org.squire.checkin.entities.AlternativeNameDAO;
import org.squire.checkin.models.AlternativeNameObject;

import java.util.List;
import java.util.stream.Collectors;

public class AlternativeNameParser {

    public static List<AlternativeNameDAO> parseAlternativeNameObjects(List<AlternativeNameObject> alternativeNameObjects, Integer personId) {
        return alternativeNameObjects.stream().map(object -> parseAlternativeNameObject(object, personId)).collect(Collectors.toList());
    }

    public static List<AlternativeNameObject> parseAlternativeNameDAOs(List<AlternativeNameDAO> alternativeNameDAOs) {
        return alternativeNameDAOs.stream().map(AlternativeNameParser::parseAlternativeNameDAO).collect(Collectors.toList());
    }

    public static AlternativeNameDAO parseAlternativeNameObject(AlternativeNameObject alternativeNameObject, Integer personId) {
        AlternativeNameDAO alternativeNameDAO = new AlternativeNameDAO();
        alternativeNameDAO.setPersonId(personId);
        alternativeNameDAO.setLanguage(alternativeNameObject.getLanguage());
        alternativeNameDAO.setAlternativeName(alternativeNameObject.getAlternativeName());
        return alternativeNameDAO;
    }

    public static AlternativeNameObject parseAlternativeNameDAO(AlternativeNameDAO alternativeNameDAO) {
        AlternativeNameObject alternativeNameObject = new AlternativeNameObject();
        alternativeNameObject.setAlternativeName(alternativeNameDAO.getAlternativeName());
        alternativeNameObject.setLanguage(alternativeNameDAO.getLanguage());
        return alternativeNameObject;
    }
}
