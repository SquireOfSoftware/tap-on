package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.ProjectConfiguration;
import com.squireofsoftware.peopleproject.dtos.*;
import com.squireofsoftware.peopleproject.entities.*;
import com.squireofsoftware.peopleproject.exceptions.DatabaseProcessingException;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaEmailAddress;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import com.squireofsoftware.peopleproject.jpas.JpaPhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Person personToBeSaved = Person.builder()
                .givenName(personObject.getGivenName())
                .familyName(personObject.getFamilyName())
                .isBaptised(personObject.getIsBaptised())
                .isMember(personObject.getIsMember())
                .creationDate(now)
                .lastModified(now)
                .hash(UUID.randomUUID().toString())
                .build();
        Person saved = jpaPerson.save(personToBeSaved);

        createOtherNames(personObject.getOtherNames(), saved);
        createPhoneNumbers(personObject.getPhoneNumbers(), saved);
        createEmailAddresses(personObject.getEmailAddresses(), saved);

        return jpaPerson.findById(saved.getId())
                .map(PersonObject::map)
                .orElseThrow(DatabaseProcessingException::new);
    }

    private void createOtherNames(List<NameObject> otherNames, Person person) {
        for(NameObject otherName: otherNames) {
            jpaNamePart.saveAndFlush(NamePart.builder()
                    .person(person)
                    .value(otherName.getName())
                    .type(Language.valueOf(otherName.getLanguage()))
                    .build());
        }
    }

    private void createPhoneNumbers(List<PhoneNumberObject> phoneNumbers, Person person) {
        for(PhoneNumberObject phoneNumber: phoneNumbers) {
            jpaPhoneNumber.saveAndFlush(PhoneNumber.builder()
                    .number(phoneNumber.getNumber())
                    .description(phoneNumber.getDescription())
                    .person(person)
                    .build());
        }
    }

    private void createEmailAddresses(List<EmailAddressObject> emails, Person person) {
        for(EmailAddressObject emailObject: emails) {
            jpaEmailAddress.saveAndFlush(EmailAddress.builder()
                    .email(emailObject.getEmail())
                    .description(emailObject.getDescription())
                    .person(person)
                    .build());
        }
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

    @Override
    public List<PersonReferenceObject> getAllPeople() {
        return jpaPerson.findAll()
                .stream()
                .map(PersonReferenceObject::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PersonObject updatePerson(Integer id, PersonObject updatedPerson) {
        Person personToBeUpdated = jpaPerson.findById(id)
                .orElseThrow(PersonNotFoundException::new);

        personToBeUpdated.setGivenName(updatedPerson.getGivenName());
        personToBeUpdated.setFamilyName(updatedPerson.getFamilyName());
        personToBeUpdated.setIsBaptised(updatedPerson.getIsBaptised());
        personToBeUpdated.setIsMember(updatedPerson.getIsMember());
        personToBeUpdated.setLastModified(new Timestamp(System.currentTimeMillis()));

        jpaPerson.save(personToBeUpdated);

        jpaNamePart.deleteAllByPersonId(personToBeUpdated.getId());
        createOtherNames(updatedPerson.getOtherNames(), personToBeUpdated);

        jpaEmailAddress.deleteAllByPersonId(personToBeUpdated.getId());
        createPhoneNumbers(updatedPerson.getPhoneNumbers(), personToBeUpdated);

        jpaPhoneNumber.deleteAllByPersonId(personToBeUpdated.getId());
        createEmailAddresses(updatedPerson.getEmailAddresses(), personToBeUpdated);

        return jpaPerson.findById(id)
                .map(PersonObject::map)
                .orElseThrow(PersonNotFoundException::new);
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
