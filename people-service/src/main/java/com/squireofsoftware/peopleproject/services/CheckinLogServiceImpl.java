package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.SignInObject;
import com.squireofsoftware.peopleproject.entities.CheckinLog;
import com.squireofsoftware.peopleproject.entities.PersonHash;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaCheckinLog;
import com.squireofsoftware.peopleproject.jpas.JpaPersonHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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

    @Override
    public CheckinLogObject checkin(SignInObject signInLogObject) {
        // find the person to see if the has exists
        // throw exception if the appropriate hash cannot be found
        // otherwise create checkinlog and return the person
        Optional<PersonHash> personHash = jpaPersonHash.findByHash(signInLogObject.getHash());
        if (personHash.isEmpty()) {
            throw new PersonNotFoundException();
        }

        return CheckinLogObject.map(jpaCheckinLog.save(CheckinLog.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .person(personHash.get().getPerson())
                .message(signInLogObject.getMessage())
                .build()));
    }

    @Override
    public List<CheckinLogObject> getLogs(Integer hash) {
        Optional<PersonHash> personHash = jpaPersonHash.findByHash(hash);
        if (personHash.isEmpty()) {
            throw new PersonNotFoundException();
        }

        return jpaCheckinLog.findAllByPersonId(personHash.get().getPerson().getId())
                .stream()
                .map(CheckinLogObject::map)
                .collect(Collectors.toList());
    }
}
