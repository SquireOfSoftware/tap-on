package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.entities.Language;
import com.squireofsoftware.peopleproject.entities.NamePart;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.entities.PersonHash;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import com.squireofsoftware.peopleproject.jpas.JpaPersonHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private final JpaPerson jpaPerson;
    @Autowired
    private final JpaNamePart jpaNamePart;
    @Autowired
    private final JpaPersonHash jpaPersonHash;

    public PersonServiceImpl(JpaPerson jpaPerson,
                             JpaNamePart jpaNamePart,
                             JpaPersonHash jpaPersonHash) {
        this.jpaPerson = jpaPerson;
        this.jpaNamePart = jpaNamePart;
        this.jpaPersonHash = jpaPersonHash;
    }

    @Transactional
    @Override
    public PersonObject createPerson(PersonObject personObject) {
        Person newPerson = Person.builder()
                .givenName(personObject.getGivenName())
                .familyName(personObject.getFamilyName())
                .isBaptised(personObject.getIsBaptised())
                .isMember(personObject.getIsMember())
                .creationDate(new Timestamp(System.currentTimeMillis()))
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();
        Person saved = jpaPerson.save(newPerson);
        personObject.setId(saved.getId());
        PersonHash hash = jpaPersonHash.save(PersonHash.builder()
                .person(saved)
                .hash(saved.hashCode())
                .build());
        personObject.setHash(hash.getHash());
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
        Optional<PersonHash> foundHash = jpaPersonHash.findByPersonId(id);
        return found.map(person -> PersonObject.builder()
                .familyName(person.getFamilyName())
                .givenName(person.getGivenName())
                .id(person.getId())
                .isBaptised(person.getIsBaptised())
                .isMember(person.getIsMember())
                .hash(foundHash.map(PersonHash::getHash).orElse(null))
                .build())
            .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public void deletePerson(Integer id) {
        jpaPerson.deleteById(id);
    }
}
