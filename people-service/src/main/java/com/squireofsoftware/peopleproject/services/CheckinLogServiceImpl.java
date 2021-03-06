package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.BulkSignInObject;
import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;
import com.squireofsoftware.peopleproject.entities.CheckinLog;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.exceptions.PeopleNotFoundException;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaCheckinLog;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckinLogServiceImpl implements CheckinLogService {
    private final JpaPerson jpaPerson;
    private final JpaCheckinLog jpaCheckinLog;

    public CheckinLogServiceImpl(JpaPerson jpaPerson,
                                 JpaCheckinLog jpaCheckinLog) {
        this.jpaPerson = jpaPerson;
        this.jpaCheckinLog = jpaCheckinLog;
    }

    private Person getPerson(String hash) {
        Optional<Person> person = jpaPerson.findByHash(hash);
        if (person.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return person.get();
    }

    @Override
    public CheckinLogObject checkin(SignInObject signInLogObject) {
        // find the person to see if the has exists
        // throw exception if the appropriate hash cannot be found
        // otherwise create checkinlog and return the person
        return CheckinLogObject.map(jpaCheckinLog.save(CheckinLog.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .person(getPerson(signInLogObject.getHash()))
                .message(signInLogObject.getMessage())
                .build()));
    }

    @Override
    public CheckinLogObject checkin(@Valid @NotNull String hash, String message) {
        return CheckinLogObject.map(jpaCheckinLog.save(
                CheckinLog.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .person(getPerson(hash))
                .message(message)
                .build()));
    }

    @Override
    public List<CheckinLogObject> getAllPersonsLogs(String hash) {
        return jpaCheckinLog.findAllByPersonId(getPerson(hash).getId())
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsFromTo(String hash, LocalDateTime from, LocalDateTime to) {
        return jpaCheckinLog.findByPersonIdAndTimestampBetween(
                    getPerson(hash).getId(),
                    Timestamp.valueOf(from),
                    Timestamp.valueOf(to))
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsFrom(String hash, LocalDateTime from) {
        return jpaCheckinLog.findByPersonIdAndTimestampAfter(
                    getPerson(hash).getId(),
                Timestamp.valueOf(from))
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsTo(String hash, LocalDateTime to) {
        return jpaCheckinLog.findByPersonIdAndTimestampBefore(
                    getPerson(hash).getId(),
                Timestamp.valueOf(to))
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getSignInsForToday() {
        return getSignInsFrom(LocalDate.now().atStartOfDay());
    }

    @Override
    public List<CheckinLogObject> getSignInsFrom(LocalDateTime from) {
        return jpaCheckinLog.findByTimestampAfter(Timestamp.valueOf(from))
                .stream()
                .collect(Collectors.toMap(
                        CheckinLog::getPerson,
                        checkinLog -> checkinLog,
                        (existing, replacement) -> {
                            if (existing.getTimestamp().after(replacement.getTimestamp())) {
                                return replacement;
                            } else {
                                return existing;
                            }
                        }))
                .values()
                .stream()
                .sorted(Comparator.comparing(CheckinLog::getTimestamp))
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> bulkCheckIn(@Valid BulkSignInObject bulkSignInObject) {
        Set<String> hashes = new HashSet<>(bulkSignInObject.getHashes());

        List<Person> foundPeople = jpaPerson.findAllByHashIn(hashes);

        if (hashes.size() != foundPeople.size()) {
            Set<String> foundHashes = foundPeople.stream()
                    .map(Person::getHash)
                    .collect(Collectors.toSet());
            hashes.removeAll(foundHashes);

            throw new PeopleNotFoundException(hashes);
        }

        return foundPeople.stream()
                .map(person -> checkin(person.getHash(), bulkSignInObject.getMessage()))
                .collect(Collectors.toList());
    }
}
