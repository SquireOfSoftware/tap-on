package org.squire.checkin.services;

import org.squire.checkin.models.PersonObject;

import java.util.List;

public interface PersonService {
    List<PersonObject> getAllPeople();
    List<PersonObject> getAllMembers();
    PersonObject createPerson(PersonObject personObject);
    boolean removePerson(Integer personId);
}
