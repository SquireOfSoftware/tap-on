package com.squireofsoftware.peopleproject.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;

import java.util.Collections;
import java.util.List;

@DgsComponent
public class PersonFetcher {
    private final JpaPerson personDao;

    public PersonFetcher(JpaPerson personDao) {
        this.personDao = personDao;
    }

    @DgsQuery
    public List<Person> people(@InputArgument Integer id) {
        if(id == null) {
            return personDao.findAll();
        }

        return personDao.findById(id)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    @DgsEntityFetcher(name = "Person")
    public Person person(@InputArgument Integer id) {
        return personDao.findById(id).orElse(null);
    }
}
