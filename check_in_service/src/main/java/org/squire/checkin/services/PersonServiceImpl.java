package org.squire.checkin.services;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.AlternativeNameDAO;
import org.squire.checkin.entities.Language;
import org.squire.checkin.entities.PersonDAO;
import org.squire.checkin.models.AlternativeNameObject;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.UpdatedDetailsObject;
import org.squire.checkin.repository.AlternativeNameRepository;
import org.squire.checkin.repository.PersonRepository;
import org.squire.checkin.repository.SignInTimeRepository;
import org.squire.checkin.utils.PersonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.squire.checkin.utils.PersonParser.parsePersonObject;

@Slf4j
@Service
public class PersonServiceImpl implements PersonService {
    private PersonRepository personRepository;
    private SignInTimeRepository signInTimeRepository;
    private AlternativeNameRepository alternativeNameRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository,
                             SignInTimeRepository signInTimeRepository,
                             AlternativeNameRepository alternativeNameRepository) {
        this.personRepository = personRepository;
        this.signInTimeRepository = signInTimeRepository;
        this.alternativeNameRepository = alternativeNameRepository;
    }

    @Override
    public List<PersonObject> getAllPeople() {
        List<PersonDAO> personList = new ArrayList<>();
        personRepository.findAll().forEach(personList::add);
        return personList.stream().map(PersonParser::parsePersonDAO).collect(Collectors.toList());
    }

    @Override
    public List<PersonObject> getAllMembers() {
        return personRepository.findAllByMemberSinceIsNotNull().stream()
                .map(PersonParser::parsePersonDAO)
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

    @Override
    public boolean hasPerson(Integer id) {
        return personRepository.findById(id).isPresent();
    }

    @Override
    public PersonObject getPerson(Integer id) {
        PersonObject personObject = PersonParser.parsePersonDAO(personRepository.findById(id).get());
        if (signInTimeRepository.findById(id).isPresent()) {
            personObject.setLastSignIn(signInTimeRepository.findById(id).get().getSignInTime());
        }
        return personObject;
    }

    private boolean updatePersonObjectDetails(Integer id, UpdatedDetailsObject updatedDetailsObject){
        PersonObject personObject = PersonParser.parsePersonDAO(personRepository.findById(id).get());
        boolean hasChanged = false;
        if (!Strings.isNullOrEmpty(updatedDetailsObject.getGivenName())) {
            personObject.setGivenName(updatedDetailsObject.getGivenName());
            hasChanged = true;
        }
        if (!Strings.isNullOrEmpty(updatedDetailsObject.getFamilyName())) {
            personObject.setFamilyName(updatedDetailsObject.getFamilyName());
            hasChanged = true;
        }
        if (updatedDetailsObject.getBaptisedSince() != null) {
            personObject.setBaptisedSince(updatedDetailsObject.getBaptisedSince());
            hasChanged = true;
        }
        if (updatedDetailsObject.getMemberSince() != null) {
            personObject.setMemberSince(updatedDetailsObject.getMemberSince());
            hasChanged = true;
        }
        if (hasChanged) {
            personRepository.save(parsePersonObject(personObject));
        }
        return hasChanged;
    }

    private boolean updateAlternativeNameDetails(Integer id, UpdatedDetailsObject updatedDetailsObject) {
        AlternativeNameObject updatedAlternativeName = updatedDetailsObject.getAlternativeName();
        boolean hasChanged = false;
        if (updatedAlternativeName != null &&
                updatedAlternativeName.getAlternativeName() != null) {
            AlternativeNameDAO alternativeNameDAO = alternativeNameRepository.findByLanguage(id, updatedDetailsObject.getAlternativeName().getLanguage());
            if (alternativeNameDAO != null) {
                alternativeNameDAO.setAlternativeName(updatedAlternativeName.getAlternativeName());
                hasChanged = true;
//            } else if (updatedAlternativeName.getLanguage() != null) {
//                alternativeNameDAO = new AlternativeNameDAO();
//                alternativeNameDAO.setLanguage(updatedAlternativeName.getLanguage());
//                alternativeNameDAO.setPersonId(id);
//                alternativeNameDAO.setAlternativeName(updatedAlternativeName.getAlternativeName());
//                hasChanged = true;
            }
            if(hasChanged) {
                alternativeNameRepository.save(alternativeNameDAO);
            }
        }

        return hasChanged;
    }

    @Override
    public boolean updatePerson(Integer id, UpdatedDetailsObject updatedDetailsObject) {
        if(personRepository.findById(id).isPresent()) {
            boolean updatedPersonDetails = updatePersonObjectDetails(id, updatedDetailsObject);
            boolean updateAlternativeNameDetails = updateAlternativeNameDetails(id, updatedDetailsObject);
            return updatedPersonDetails || updateAlternativeNameDetails;
        }
        return false;
    }
}
