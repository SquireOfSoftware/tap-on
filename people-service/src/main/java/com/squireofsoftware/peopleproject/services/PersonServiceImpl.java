package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.ProjectConfiguration;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PersonServiceImpl implements PersonService {
    private final JpaPerson jpaPerson;
    private final JpaNamePart jpaNamePart;
    private final JpaPhoneNumber jpaPhoneNumber;
    private final JpaEmailAddress jpaEmailAddress;
    private final ProjectConfiguration projectConfiguration;

    public PersonServiceImpl(JpaPerson jpaPerson,
                             JpaNamePart jpaNamePart,
                             JpaEmailAddress jpaEmailAddress,
                             JpaPhoneNumber jpaPhoneNumber,
                             ProjectConfiguration projectConfiguration) {
        this.jpaPerson = jpaPerson;
        this.jpaNamePart = jpaNamePart;
        this.jpaEmailAddress = jpaEmailAddress;
        this.jpaPhoneNumber = jpaPhoneNumber;
        this.projectConfiguration = projectConfiguration;
    }

    @Transactional
    @Override
    public PersonObject createPerson(PersonObject personObject) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Person newPerson = Person.builder()
                .givenName(personObject.getGivenName())
                .familyName(personObject.getFamilyName())
                .isBaptised(personObject.getIsBaptised())
                .isMember(personObject.getIsMember())
                .creationDate(now)
                .lastModified(now)
                .hash(UUID.randomUUID().toString())
                .build();
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
        return found.map(PersonObject::map)
            .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public PersonObject findPersonByHash(String hash) {
        Optional<Person> found = jpaPerson.findByHash(hash);
        return found.map(PersonObject::map)
                .orElseThrow(PersonNotFoundException::new);
    }

    @Override
    public void deletePerson(Integer id) {
        jpaPerson.deleteById(id);
    }

    @Transactional
    @Override
    public PersonObject recreateHash(Integer id) {
        return jpaPerson.findById(id)
                .map(this::updateHash)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    private PersonObject updateHash(Person person) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        person.setLastModified(now);
        String newHash = UUID.randomUUID().toString();
        int hashRegenerationCount = 1;
        // there could be an infinite loop here
        while(newHash.equals(person.getHash()) &&
                hashRegenerationCount < projectConfiguration.getMaxHashRegenCount()) {
            now = new Timestamp(System.currentTimeMillis());
            person.setLastModified(now);
            newHash = UUID.randomUUID().toString();
            hashRegenerationCount++;
        }

        if(hashRegenerationCount == projectConfiguration.getMaxHashRegenCount()) {
            log.warn("For some strange reason the hash did not change for person: {}", person.getId());
        }

        person.setHash(newHash);
        return PersonObject.map(jpaPerson.save(person));
    }
}
