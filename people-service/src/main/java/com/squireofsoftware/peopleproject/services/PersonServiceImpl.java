package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.EmailAddressObject;
import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PhoneNumberObject;
import com.squireofsoftware.peopleproject.entities.*;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaEmailAddress;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import com.squireofsoftware.peopleproject.jpas.JpaPhoneNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {
    private final JpaPerson jpaPerson;
    private final JpaNamePart jpaNamePart;
    private final JpaPhoneNumber jpaPhoneNumber;
    private final JpaEmailAddress jpaEmailAddress;

    public PersonServiceImpl(JpaPerson jpaPerson,
                             JpaNamePart jpaNamePart,
                             JpaEmailAddress jpaEmailAddress,
                             JpaPhoneNumber jpaPhoneNumber) {
        this.jpaPerson = jpaPerson;
        this.jpaNamePart = jpaNamePart;
        this.jpaEmailAddress = jpaEmailAddress;
        this.jpaPhoneNumber = jpaPhoneNumber;
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
        newPerson.setHash(newPerson.hashCode());
        Person saved = jpaPerson.save(newPerson);
        personObject.setId(saved.getId());

        for(NameObject otherName: personObject.getOtherNames()) {
            jpaNamePart.save(NamePart.builder()
                .person(saved)
                .value(otherName.getName())
                .type(Language.valueOf(otherName.getLanguage()))
                .build());
        }

        for(PhoneNumberObject phoneNumber: personObject.getPhoneNumbers()) {
            jpaPhoneNumber.save(PhoneNumber.builder()
                    .number(phoneNumber.getNumber())
                    .description(phoneNumber.getDescription())
                    .person(newPerson)
                    .build());
        }

        for(EmailAddressObject emailObject: personObject.getEmailAddresses()) {
            jpaEmailAddress.save(EmailAddress.builder()
                    .email(emailObject.getEmail())
                    .description(emailObject.getDescription())
                    .person(newPerson)
                    .build());
        }

        return personObject;
    }

    @Transactional(readOnly = true)
    @Override
    public PersonObject getPerson(Integer id) {
        Optional<Person> found = jpaPerson.findById(id);
        return found.map(this::mapTo)
            .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public PersonObject findPersonByHash(Integer hash) {
        Optional<Person> found = jpaPerson.findByHash(hash);
        return found.map(this::mapTo)
                .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public void deletePerson(Integer id) {
        jpaPerson.deleteById(id);
    }

    private PersonObject mapTo(Person person) {
        return PersonObject.builder()
                .familyName(person.getFamilyName())
                .givenName(person.getGivenName())
                .id(person.getId())
                .isBaptised(person.getIsBaptised())
                .isMember(person.getIsMember())
                .hash(person.getHash())
                .emailAddresses(jpaEmailAddress.findByPersonId(person.getId())
                        .stream()
                        .map(EmailAddressObject::mapFrom)
                        .collect(Collectors.toList()))
                .phoneNumbers(jpaPhoneNumber.findByPersonId(person.getId())
                        .stream()
                        .map(PhoneNumberObject::mapFrom)
                        .collect(Collectors.toList()))
                .otherNames(jpaNamePart.findByPersonId(person.getId())
                        .stream()
                        .map(NameObject::mapFrom)
                        .collect(Collectors.toList()))
                .build();
    }
}
