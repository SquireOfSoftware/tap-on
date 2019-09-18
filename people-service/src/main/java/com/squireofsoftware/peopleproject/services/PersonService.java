package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.PersonObject;

public interface PersonService {
    /**
     * This method will create duplicate people, if you are not careful
     */
    PersonObject createPerson(PersonObject personObject);

    PersonObject getPerson(Integer id);

    void deletePerson(Integer id);
}
