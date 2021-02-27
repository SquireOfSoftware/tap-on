package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PersonReferenceObject;

import java.util.List;

public interface PersonService {
    /**
     * This method will create duplicate people, if you are not careful
     */
    PersonObject createPerson(PersonObject personObject);

    PersonObject getPerson(Integer id);

    PersonObject findPersonByHash(String hash);

    void deletePerson(Integer id);

    PersonObject recreateHash(Integer id);

    List<PersonReferenceObject> getAllPeople();
}
