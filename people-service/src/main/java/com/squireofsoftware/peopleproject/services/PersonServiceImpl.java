package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.entities.Language;
import com.squireofsoftware.peopleproject.entities.NamePart;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private JpaPerson jpaPerson;
    @Autowired
    private JpaNamePart jpaNamePart;

    public PersonServiceImpl(JpaPerson jpaPerson,
                             JpaNamePart jpaNamePart) {
        this.jpaPerson = jpaPerson;
        this.jpaNamePart = jpaNamePart;
    }

    @Transactional
    @Override
    public PersonObject createPerson(PersonObject personObject) {
        Person saved = jpaPerson.save(Person.builder()
                .givenName(personObject.getGivenName())
                .familyName(personObject.getFamilyName())
                .isBaptised(personObject.getIsBaptised())
                .isMember(personObject.getIsMember())
                .build());
        personObject.setId(saved.getId());
        for(NameObject otherName: personObject.getOtherNames()) {
            jpaNamePart.save(NamePart.builder()
                .personId(saved.getId())
                .value(otherName.getValue())
                .type(Language.valueOf(otherName.getLanguage()))
                .build());
        }
        return personObject;
    }

    @Override
    public PersonObject getPerson(Integer id) {
        Optional<Person> found = jpaPerson.findById(id);
        return found.map(person -> PersonObject.builder()
            .familyName(person.getFamilyName())
            .givenName(person.getGivenName())
            .id(person.getId())
            .isBaptised(person.getIsBaptised())
            .isMember(person.getIsMember())
            .build())
        .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public void deletePerson(Integer id) {
        jpaPerson.deleteById(id);
    }
}
