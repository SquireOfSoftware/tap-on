package org.squire.checkin.utils;

import org.squire.checkin.entities.PersonDAO;
import org.squire.checkin.models.PersonObject;

import java.util.Optional;

public class PersonParser {
    public static Optional<PersonObject> parsePersonDAO(Optional<PersonDAO> personDAO) {
        return personDAO.map(PersonParser::parsePersonDAO);
    }

    public static PersonObject parsePersonDAO(PersonDAO personDAO) {
        PersonObject personObject = new PersonObject();
        personObject.setPersonId(personDAO.getId());
        personObject.setFamilyName(personDAO.getFamilyName());
        personObject.setGivenName(personDAO.getGivenName());
        personObject.setMemberSince(personDAO.getMemberSince());
        return personObject;
    }

    public static PersonDAO parsePersonObject(PersonObject personObject) {
        PersonDAO personDAO = new PersonDAO();
        personDAO.setGivenName(personObject.getGivenName());
        personDAO.setFamilyName(personObject.getFamilyName());
        personDAO.setMemberSince(personObject.getMemberSince());
        personDAO.setBaptisedSince(personObject.getBaptisedSince());
        return personDAO;
    }
}
