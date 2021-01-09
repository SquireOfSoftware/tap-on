package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.entities.CheckinLog;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.jpas.JpaCheckinLog;
import com.squireofsoftware.peopleproject.jpas.JpaPersonHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CheckinLogServiceImplTest {
    @Mock
    private JpaCheckinLog mockJpaCheckinLog;
    @Mock
    private JpaPersonHash mockJpaPersonHash;

    private CheckinLogServiceImpl checkinLogService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        checkinLogService = new CheckinLogServiceImpl(mockJpaPersonHash, mockJpaCheckinLog);
    }

    @Test
    public void getSignInsForToday_returnsLogsSortedByDates_inEarliestFirst() {
        // given
        Person dummyPerson1 = Person.builder()
                .familyName("One")
                .givenName("Person")
                .build();

        LocalDateTime person1SignIn = LocalDateTime.now()
                .minusHours(1L);

        Person dummyPerson2 = Person.builder()
                .familyName("Two")
                .givenName("Person")
                .build();

        LocalDateTime person2SignIn = LocalDateTime.now()
                .minusHours(2L);

        when(mockJpaCheckinLog.findByTimestampAfter(any()))
                .thenReturn(Arrays.asList(
                        CheckinLog.builder()
                                .person(dummyPerson2)
                                .timestamp(Timestamp.valueOf(person2SignIn))
                                .build(),
                        CheckinLog.builder()
                                .person(dummyPerson1)
                                .timestamp(Timestamp.valueOf(person1SignIn))
                                .build()
                ));

        // when
        List<CheckinLogObject> results = checkinLogService.getSignInsForToday();

        // then
        assertEquals(2, results.size());
        assertEquals(PersonObject.map(dummyPerson2), results.get(0).getPerson());
        assertEquals(PersonObject.map(dummyPerson1), results.get(1).getPerson());
    }

    @Test
    public void getSignInsForToday_returnsEarliestTimestamp_whenPersonHasDuplicateSignins() {
        // given
        Person dummyPerson1 = Person.builder()
                .familyName("One")
                .givenName("Person")
                .build();

        LocalDateTime person1SignIn = LocalDateTime.now()
                .minusHours(2L);

        LocalDateTime person1LaterSignIn = LocalDateTime.now()
                .minusHours(1L);

        LocalDateTime person1LatestSignIn = LocalDateTime.now();

        when(mockJpaCheckinLog.findByTimestampAfter(any()))
                .thenReturn(Arrays.asList(
                        CheckinLog.builder()
                                .person(dummyPerson1)
                                .timestamp(Timestamp.valueOf(person1LaterSignIn))
                                .build(),
                        CheckinLog.builder()
                                .person(dummyPerson1)
                                .timestamp(Timestamp.valueOf(person1SignIn))
                                .build(),
                        CheckinLog.builder()
                                .person(dummyPerson1)
                                .timestamp(Timestamp.valueOf(person1LatestSignIn))
                                .build()
                ));

        // when
        List<CheckinLogObject> results = checkinLogService.getSignInsForToday();

        // then
        assertEquals(1, results.size());
        assertEquals(PersonObject.map(dummyPerson1), results.get(0).getPerson());
        assertEquals(person1SignIn, results.get(0).getTimestamp());
    }
}