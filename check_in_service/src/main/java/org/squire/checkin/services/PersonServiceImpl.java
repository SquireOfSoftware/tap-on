package org.squire.checkin.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.PersonDAO;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PersonServiceImpl implements PersonService {
    private PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    private PersonObject parsePersonDAO(PersonDAO personDAO) {
        PersonObject personObject = new PersonObject();
        personObject.setPersonId(personDAO.getId());
        personObject.setFamilyName(personDAO.getFamilyName());
        personObject.setGivenName(personDAO.getGivenName());
        personObject.setMemberSince(personDAO.getMemberSince());
        return personObject;
    }

    private PersonDAO parsePersonObject(PersonObject personObject) {
        PersonDAO personDAO = new PersonDAO();
        personDAO.setGivenName(personObject.getGivenName());
        personDAO.setFamilyName(personObject.getFamilyName());
        personDAO.setMemberSince(personObject.getMemberSince());
        return personDAO;
    }

    @Override
    public List<PersonObject> getAllPeople() {
        List<PersonDAO> personList = new ArrayList<>();
        personRepository.findAll().forEach(personList::add);
        return personList.stream().map(this::parsePersonDAO).collect(Collectors.toList());
    }

    @Override
    public List<PersonObject> getAllMembers() {
        return personRepository.findAllByMemberSinceIsNotNull().stream()
                .map(this::parsePersonDAO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonObject createPerson(PersonObject personObject) {
        PersonDAO savedPersonDAO = personRepository.save(parsePersonObject(personObject));
        personObject.setPersonId(savedPersonDAO.getId());
        return personObject;
    }

    @Override
    public boolean removePerson(Integer personId) {
        if(personRepository.findById(personId).isPresent()) {
            personRepository.deleteById(personId);
            return true;
        }
        return false;
    }
}
