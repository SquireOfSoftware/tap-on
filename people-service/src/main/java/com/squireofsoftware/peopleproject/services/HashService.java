package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.PersonObject;

public interface HashService {
    Integer getHash(Integer personId);

    PersonObject getPerson(Integer hash);
}
