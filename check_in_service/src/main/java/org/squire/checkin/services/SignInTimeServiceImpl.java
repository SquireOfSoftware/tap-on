package org.squire.checkin.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.SignInDAO;
import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.SignOutObject;
import org.squire.checkin.repository.SignInTimeRepository;
import org.squire.checkin.utils.SignInParser;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.squire.checkin.utils.SignInParser.TIME_COLUMN_NAME;

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
    public SignInObject getLatestSignIn(Integer personId) {
        Optional<SignInDAO> signIn = signInTimeRepository.findLatestSignInTime(
                personId,
                PageRequest.of(0, 1, Sort.Direction.DESC, TIME_COLUMN_NAME))
                .stream().findFirst();
        return signIn.map(SignInParser::parseSignInDAO).orElse(null);
    }

    @Override
    public MessageObject signInPersonId(Integer id) {
        SignInDAO signInDAO = new SignInDAO();
        signInDAO.setPersonId(id);
        signInDAO.setSignInTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        signInTimeRepository.save(signInDAO);
        return new MessageObject(true, "Successfully signed in");
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
                    successes.incrementAndGet();
                    return SignInParser.parseSignInObject(signInObject);
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
