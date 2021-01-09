package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;
import com.squireofsoftware.peopleproject.entities.CheckinLog;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.entities.PersonHash;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaCheckinLog;
import com.squireofsoftware.peopleproject.jpas.JpaPersonHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckinLogServiceImpl implements CheckinLogService {
    @Autowired
    private final JpaPersonHash jpaPersonHash;
    @Autowired
    private final JpaCheckinLog jpaCheckinLog;

    public CheckinLogServiceImpl(JpaPersonHash jpaPersonHash,
                                 JpaCheckinLog jpaCheckinLog) {
        this.jpaPersonHash = jpaPersonHash;
        this.jpaCheckinLog = jpaCheckinLog;
    }

    private Person getPerson(Integer hash) {
        Optional<PersonHash> personHash = jpaPersonHash.findByHash(hash);
        if (personHash.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return personHash.get().getPerson();
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
    public List<CheckinLogObject> getAllPersonsLogs(Integer hash) {
        return jpaCheckinLog.findAllByPersonId(getPerson(hash).getId())
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsFromTo(Integer hash, LocalDateTime from, LocalDateTime to) {
        return jpaCheckinLog.findByPersonIdAndTimestampBetween(
                    getPerson(hash).getId(),
                    Timestamp.valueOf(from),
                    Timestamp.valueOf(to))
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsFrom(Integer hash, LocalDateTime from) {
        return jpaCheckinLog.findByPersonIdAndTimestampAfter(
                    getPerson(hash).getId(),
                Timestamp.valueOf(from))
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckinLogObject> getPersonsLogsTo(Integer hash, LocalDateTime to) {
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
}
