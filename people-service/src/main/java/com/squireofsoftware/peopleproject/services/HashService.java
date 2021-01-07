package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.entities.Person;

public interface HashService {
    String createHash(Person person);

    String getHash(Person person);

    Person getPerson(String hash);
}
