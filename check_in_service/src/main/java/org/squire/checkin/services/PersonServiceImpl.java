package org.squire.checkin.services;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.AlternativeNameDAO;
import org.squire.checkin.entities.PersonDAO;
import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.AlternativeNameObject;
import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.UpdatedDetailsObject;
import org.squire.checkin.repository.AlternativeNameRepository;
import org.squire.checkin.repository.PersonRepository;
import org.squire.checkin.repository.SignInTimeRepository;
import org.squire.checkin.utils.AlternativeNameParser;
import org.squire.checkin.utils.PersonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.squire.checkin.utils.PersonParser.parsePersonObject;
import static org.squire.checkin.utils.SignInParser.TIME_COLUMN_NAME;

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
        return personList.stream()
                .map(personDAO -> locatePerson(personDAO.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonObject> getAllMembers() {
        return personRepository.findAllByMemberSinceIsNotNull().stream()
                .map(personDAO -> locatePerson(personDAO.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public PersonObject createPerson(PersonObject personObject) {
        PersonDAO savedPersonDAO = personRepository.save(parsePersonObject(personObject));
        List<AlternativeNameObject> alternativeNames = personObject.getAlternativeNames();
        if (!alternativeNames.isEmpty()) {
            alternativeNameRepository.saveAll(AlternativeNameParser.parseAlternativeNameObjects(personObject.getAlternativeNames(), personObject.getPersonId()));
        }

        personObject.setPersonId(savedPersonDAO.getId());
        return personObject;
    }

    @Override
    public boolean removePerson(Integer personId) {
        if(personRepository.findById(personId).isPresent()) {
            alternativeNameRepository.deleteByPersonId(personId);
            signInTimeRepository.deleteByPersonId(personId);
            personRepository.deleteById(personId);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPerson(Integer id) {
        return personRepository.findById(id).isPresent();
    }

    private void locateAlternativeNames(PersonObject personObject) {
        List<AlternativeNameObject> alternativeNames =
                AlternativeNameParser.parseAlternativeNameDAOs(alternativeNameRepository.findByPerson(personObject.getPersonId()));
        if (!alternativeNames.isEmpty()) {
            personObject.setAlternativeNames(alternativeNames);
        }
    }

    private void locateLatestSignIn(PersonObject personObject) {
        Optional<SignInDAO> lastSignIn = signInTimeRepository.findLatestSignInTime(
                personObject.getPersonId(),
                PageRequest.of(0, 1, Sort.Direction.DESC, TIME_COLUMN_NAME))
                .stream().findFirst();

        lastSignIn.ifPresent(signInDAO -> personObject.setLastSignIn(signInDAO.getSignInTime()));
    }

    private PersonObject locatePerson(Integer id) {
        Optional<PersonDAO> locatedPersonDAO = personRepository.findById(id);
        if (locatedPersonDAO.isPresent()) {
            PersonObject personObject = PersonParser.parsePersonDAO(locatedPersonDAO.get());

            locateAlternativeNames(personObject);
            locateLatestSignIn(personObject);

            return personObject;
        }
        return null;
    }

    @Override
    public PersonObject getPerson(Integer id) {
        Optional<PersonObject> personObject = PersonParser.parsePersonDAO(personRepository.findById(id));
        if (personObject.isPresent()) {
            locateLatestSignIn(personObject.get());
            locateAlternativeNames(personObject.get());
            return personObject.get();
        }
        return null;
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

    private boolean updateAlternativeNameDetails(Integer personId, UpdatedDetailsObject updatedDetailsObject) {
        List<AlternativeNameObject> updatedAlternativeNames = updatedDetailsObject.getAlternativeNames();
        AtomicBoolean hasChanged = new AtomicBoolean(false);

        if (updatedAlternativeNames != null) {
            updatedAlternativeNames.forEach(alternativeNameObject -> {
                AlternativeNameDAO alternativeNameDAO = alternativeNameRepository.findByLanguage(personId, alternativeNameObject.getLanguage());
                if (alternativeNameDAO == null) {
                    alternativeNameDAO = AlternativeNameParser.parseAlternativeNameObject(alternativeNameObject, personId);
                } else {
                    alternativeNameDAO.setAlternativeName(alternativeNameObject.getAlternativeName());
                }
                hasChanged.set(true);
                alternativeNameRepository.save(alternativeNameDAO);
            });
        }

        return hasChanged.get();
    }

    @Override
    public MessageObject updatePerson(Integer id, UpdatedDetailsObject updatedDetailsObject) {
        if(personRepository.findById(id).isPresent()) {
            boolean updatedPersonDetails = updatePersonObjectDetails(id, updatedDetailsObject);
            boolean updateAlternativeNameDetails = updateAlternativeNameDetails(id, updatedDetailsObject);
            return new MessageObject(updatedPersonDetails || updateAlternativeNameDetails, "Details were updated");
        }
        return new MessageObject(false, "Could not find person with the id");
    }
}
