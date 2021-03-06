package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.BulkSignInObject;
import com.squireofsoftware.peopleproject.dtos.CheckinLogObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.entities.CheckinLog;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.exceptions.PeopleNotFoundException;
import com.squireofsoftware.peopleproject.jpas.JpaCheckinLog;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CheckinLogServiceImplTest {
    @Mock
    private JpaCheckinLog mockJpaCheckinLog;
    @Mock
    private JpaPerson mockJpaPerson;

    private CheckinLogServiceImpl checkinLogService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        checkinLogService = new CheckinLogServiceImpl(mockJpaPerson, mockJpaCheckinLog);
    }

    @Test
    public void getSignInsForToday_returnsLogsSortedByDates_inEarliestFirst() {
        // given
        Person dummyPerson1 = Person.builder()
                .id(1)
                .hash("hash1")
                .familyName("One")
                .givenName("Person")
                .build();

        LocalDateTime person1SignIn = LocalDateTime.now()
                .minusHours(1L);

        Person dummyPerson2 = Person.builder()
                .id(2)
                .hash("hash2")
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
                .id(1)
                .hash("hash1")
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

    @Test
    public void bulkCheckIn_willThrowException_andReturnNonExistentHashes_whenSomeHashesDoNotExist() {
        // given
        Person mockPerson = Person.builder()
                .hash("2")
                .build();

        when(mockJpaPerson.findAllByHashIn(any()))
                .thenReturn(Collections.singletonList(mockPerson));

        // when
        BulkSignInObject bulkSignInObject = BulkSignInObject.builder()
                .hash("1")
                .hash("2")
                .hash("3")
                .build();
        PeopleNotFoundException exception = assertThrows(PeopleNotFoundException.class, () -> checkinLogService.bulkCheckIn(bulkSignInObject));

        // then
        assertNotNull(exception);
        assertEquals(Set.of("1", "3"), exception.getMissingHashes());
    }
}