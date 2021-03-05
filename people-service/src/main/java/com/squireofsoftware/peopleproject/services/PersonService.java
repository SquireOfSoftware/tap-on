package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PersonReferenceObject;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonService {
    /**
     * This method will create duplicate people, if you are not careful
     */
    PersonObject createPerson(PersonObject personObject);

    PersonObject getPerson(Integer id);

    PersonObject findPersonByHash(String hash);

    void deletePerson(Integer id);

    PersonObject recreateHash(Integer id);

    List<PersonReferenceObject> getAllPeople();

    List<PersonReferenceObject> getAllPeopleFrom(LocalDateTime fromDate);

    /**
     * This will only update the following:
     * <ul>
     *  <li>givenName</li>
     *  <li>familyName</li>
     *  <li>otherNames <-- it will clear the old details out</li>
     *  <li>phoneNumbers <-- it will clear the old details out</li>
     *  <li>emailAddresses <-- it will clear the old details out</li>
     *  <li>isBaptised</li>
     *  <li>isAMember</li>
     *  <li>modifiedDate <-- this is automatic</li>
     * </ul>
     * Note that if nothing changed it will still update the object
     * @return the latest person details
     * @throws com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException if 'id' not legit
     */
    PersonObject updatePerson(Integer id, PersonObject updatedPerson);
}
