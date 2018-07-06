package org.squire.checkin.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.repository.SignInTimeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SignInTimeServiceImpl implements SignInTimeService {
    private SignInTimeRepository signInTimeRepository;

    @Autowired
    public SignInTimeServiceImpl(SignInTimeRepository signInTimeRepository) {
        this.signInTimeRepository = signInTimeRepository;
    }

    @Override
    public List<SignInDAO> getLatestSignIns() {
        List<SignInDAO> signInList = new ArrayList<>();
        signInTimeRepository.findAll().forEach(signInList::add);
        return signInList;
    }

    @Override
    public boolean addSignIns(List<SignInObject> signIns) {
        // you have a bunch of signins
        // return the ones that have failed, use HTTP 409
        AtomicInteger successes = new AtomicInteger();
        signInTimeRepository.saveAll(signIns.stream().map(signInObject -> {
            SignInDAO signInDAO = new SignInDAO();
            signInDAO.setPersonId(signInObject.getPersonId());
            signInDAO.setSignInTime(signInObject.getSignInTime());
            successes.incrementAndGet();
            return signInDAO;
        }).collect(Collectors.toList()));

        return successes.get() == signIns.size();
    }
}
