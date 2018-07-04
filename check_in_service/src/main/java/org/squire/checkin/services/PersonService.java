package org.squire.checkin.services;

import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.UpdatedDetailsObject;

import java.util.List;

public interface PersonService {
    List<PersonObject> getAllPeople();
    List<PersonObject> getAllMembers();
    PersonObject createPerson(PersonObject personObject);
    boolean removePerson(Integer personId);
    boolean hasPerson(Integer id);
    PersonObject getPerson(Integer id);
    boolean updatePerson(Integer id, UpdatedDetailsObject updatedDetailsObject);
}
