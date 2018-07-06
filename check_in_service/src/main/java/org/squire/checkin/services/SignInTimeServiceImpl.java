package org.squire.checkin.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.SignOutObject;
import org.squire.checkin.repository.SignInTimeRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SignInTimeServiceImpl implements SignInTimeService {
    private SignInTimeRepository signInTimeRepository;
    private int sec = 600;

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
    public MessageObject addSignIns(List<SignInObject> signIns) {
        // you have a bunch of signins
        // return the ones that have failed, use HTTP 409
        AtomicInteger successes = new AtomicInteger();
        signInTimeRepository.saveAll(signIns.stream()
                // filter is to check if the person has already signed in or not
                // ideally there should be no records for the given person at that given time + or - 10 seconds
                .filter(signInObject ->
                        signInTimeRepository.findBySignInTimesBetween(
                                signInObject.getPersonId(),
                                new Timestamp(signInObject.getSignInTime().getTime() - (sec * 1000L)),
                                new Timestamp(signInObject.getSignInTime().getTime() + (sec * 1000L))).size() == 0)
                // with those who haven't signed in, create the sign in entries
                .map(signInObject -> {
                    SignInDAO signInDAO = new SignInDAO();
                    signInDAO.setPersonId(signInObject.getPersonId());
                    signInDAO.setSignInTime(signInObject.getSignInTime());
                    successes.incrementAndGet();
                    return signInDAO;
                }).collect(Collectors.toList()));

        if (successes.get() == signIns.size()) {
            return new MessageObject(true, "Successfully signed in everyone");
        }
        return new MessageObject(false, "There were some issues signing in some people");
    }

    /**
     * Assumes that there is only one sign in per day
     * @param signOuts a list of ids of people who want to sign out
     */
    @Override
    public MessageObject removeSignIns(List<SignOutObject> signOuts) {
        signOuts.forEach(signOutRequest -> {
            List<SignInDAO> signIns = signInTimeRepository.findBySignInTimesBetween(signOutRequest.getPersonId(), signOutRequest.getBeforeDate(), signOutRequest.getAfterDate());
            signInTimeRepository.deleteAll(signIns);
        });
        return new MessageObject(true, "Signs were successfully removed");
    }
}
