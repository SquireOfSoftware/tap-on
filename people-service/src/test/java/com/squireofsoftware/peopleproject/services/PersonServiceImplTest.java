package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.ProjectConfiguration;
import com.squireofsoftware.peopleproject.entities.Person;
import com.squireofsoftware.peopleproject.jpas.JpaEmailAddress;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import com.squireofsoftware.peopleproject.jpas.JpaPhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PersonServiceImplTest {
    @Mock
    private JpaPerson mockJpaPerson;
    @Mock
    private JpaNamePart mockJpaNamePart;
    @Mock
    private JpaPhoneNumber mockJpaPhoneNumber;
    @Mock
    private JpaEmailAddress mockJpaEmailAddress;
    private PersonServiceImpl personService;

    @BeforeEach
    public void setup() {
        openMocks(this);
        ProjectConfiguration dummyConfig = new ProjectConfiguration(10);
        personService = new PersonServiceImpl(
                mockJpaPerson,
                mockJpaNamePart,
                mockJpaEmailAddress,
                mockJpaPhoneNumber,
                dummyConfig);
    }

    @Test
    public void recreateHash_shouldCreateNewHashCodes() {
        // given
        Timestamp createdTimestamp =
                new Timestamp(System.currentTimeMillis());
        Person mockPerson = Person.builder()
                .id(1)
                .givenName("Test")
                .familyName("Boy")
                .creationDate(createdTimestamp)
                .lastModified(createdTimestamp)
                .build();
        int dummyHash = mockPerson.hashCode();
        mockPerson.setHash(dummyHash);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // this is just some return value
        Person dummySavedPerson = Person.builder()
                .id(mockPerson.getId())
                .givenName(mockPerson.getGivenName())
                .familyName(mockPerson.getFamilyName())
                .creationDate(mockPerson.getCreationDate())
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .build();
        dummySavedPerson.setHash(dummySavedPerson.hashCode());

        when(mockJpaPerson.save(any()))
                .thenReturn(dummySavedPerson);

        // when
        personService.recreateHash(mockPerson.getId());

        // then
        verify(mockJpaPerson, times(1))
                .findById(eq(mockPerson.getId()));

        ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        verify(mockJpaPerson, times(1))
                .save(personArgumentCaptor.capture());
        Person savedPerson = personArgumentCaptor.getValue();

        assertNotNull(savedPerson.getHash());
        assertNotEquals(dummyHash, savedPerson.getHash());

        assertTrue(createdTimestamp.before(savedPerson.getLastModified()));
    }
}