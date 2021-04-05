package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.ProjectConfiguration;
import com.squireofsoftware.peopleproject.dtos.EmailAddressObject;
import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PhoneNumberObject;
import com.squireofsoftware.peopleproject.entities.*;
import com.squireofsoftware.peopleproject.exceptions.DatabaseProcessingException;
import com.squireofsoftware.peopleproject.jpas.JpaEmailAddress;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPerson;
import com.squireofsoftware.peopleproject.jpas.JpaPhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.util.*;

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
        ProjectConfiguration dummyConfig = new ProjectConfiguration(10, 250, 250);
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
                .hash(UUID.randomUUID().toString())
                .build();

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // this is just some return value
        Person dummySavedPerson = Person.builder()
                .id(mockPerson.getId())
                .givenName(mockPerson.getGivenName())
                .familyName(mockPerson.getFamilyName())
                .creationDate(mockPerson.getCreationDate())
                .lastModified(new Timestamp(System.currentTimeMillis()))
                .hash(UUID.randomUUID().toString())
                .build();

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
        assertNotEquals(dummySavedPerson.getHash(), savedPerson.getHash());

        assertTrue(createdTimestamp.before(savedPerson.getLastModified()));
    }

    @Test
    public void createPerson_shouldNotUseInputId() {
        // given
        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertNotEquals(dummyPerson.getId(), newPerson.getId());
    }

    @Test
    public void createPerson_shouldThrowException_whenDatabaseFailsToFindNewlyCreatedPerson() {
        // given
        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.empty());

        // when - then
        assertThrows(DatabaseProcessingException.class, () -> personService.createPerson(dummyPerson));
    }

    @Test
    public void createPerson_shouldNotCreateOtherNames_whenInputOtherNamesAreEmpty() {
        // given
        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(Collections.emptyList(), newPerson.getOtherNames());
        verifyNoInteractions(mockJpaNamePart);
    }

    @Test
    public void createPerson_shouldCreateOtherNames_whenInputOtherNamesAreNotEmpty() {
        // given
        List<NameObject> otherNames = Arrays.asList(
                NameObject.builder()
                        .name("hello")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("world")
                        .language(Language.English.name())
                        .build());

        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .otherNames(otherNames)
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        when(mockJpaNamePart.saveAndFlush(any()))
                .thenReturn(NamePart.builder()
                        .value(otherNames.get(0).getName())
                        .type(Language.English)
                        .build())
                .thenReturn(NamePart.builder()
                        .value(otherNames.get(1).getName())
                        .type(Language.English)
                        .build());

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(otherNames.size(), newPerson.getOtherNames().size());
        for (int i = 0; i < otherNames.size(); i++) {
            assertEquals(otherNames.get(i).getName(), newPerson.getOtherNames().get(i).getName());
        }
        verify(mockJpaNamePart, times(otherNames.size())).saveAndFlush(any());
    }

    @Test
    public void createPerson_shouldNotCreatePhoneNumbers_whenPhoneNumbersAreEmpty() {
        // given
        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(Collections.emptyList(), newPerson.getPhoneNumbers());
        verifyNoInteractions(mockJpaPhoneNumber);
    }

    @Test
    public void createPerson_shouldCreatePhoneNumbers_whenPhoneNumbersAreNotEmpty() {
        // given
        List<PhoneNumberObject> phoneNumbers = Arrays.asList(
                PhoneNumberObject.builder()
                        .number("+614123 123 12")
                        .build(),
                PhoneNumberObject.builder()
                        .number("+2 9999 1111")
                        .build());

        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .phoneNumbers(phoneNumbers)
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        when(mockJpaPhoneNumber.saveAndFlush(any()))
                .thenReturn(
                        PhoneNumber.builder()
                                .number(phoneNumbers.get(0).getNumber())
                                .build(),
                        PhoneNumber.builder()
                                .number(phoneNumbers.get(1).getNumber())
                                .build());

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(phoneNumbers.size(), newPerson.getPhoneNumbers().size());
        for (int i = 0; i < phoneNumbers.size(); i++) {
            assertEquals(phoneNumbers.get(i).getNumber(), newPerson.getPhoneNumbers().get(i).getNumber());
        }
        verify(mockJpaPhoneNumber, times(phoneNumbers.size())).saveAndFlush(any());
    }

    @Test
    public void createPerson_shouldNotCreateEmailAddresses_whenEmailAddressesAreEmpty() {
        // given
        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(Collections.emptyList(), newPerson.getEmailAddresses());
        verifyNoInteractions(mockJpaEmailAddress);
    }

    @Test
    public void createPerson_shouldCreateEmailAddresses_whenEmailAddressesAreNotEmpty() {
        // given
        List<EmailAddressObject> emails = Arrays.asList(
                EmailAddressObject.builder()
                        .email("test@test.com")
                        .build(),
                EmailAddressObject.builder()
                        .email("otheremail@otherdomain.com")
                        .build());

        PersonObject dummyPerson = PersonObject.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .emailAddresses(emails)
                .build();

        Person mockPerson = Person.builder()
                .id(25) // new id
                .givenName(dummyPerson.getGivenName())
                .familyName(dummyPerson.getFamilyName())
                .hash("some-hash-123")
                .build();

        when(mockJpaPerson.save(any()))
                .thenReturn(mockPerson);

        when(mockJpaPerson.findById(eq(mockPerson.getId())))
                .thenReturn(Optional.of(mockPerson));

        when(mockJpaEmailAddress.saveAndFlush(any()))
                .thenReturn(
                        EmailAddress.builder()
                                .email(emails.get(0).getEmail())
                                .build(),
                        EmailAddress.builder()
                                .email(emails.get(1).getEmail())
                                .build());

        // when
        PersonObject newPerson = personService.createPerson(dummyPerson);

        // then
        assertNotNull(newPerson);
        assertEquals(emails.size(), newPerson.getEmailAddresses().size());
        for (int i = 0; i < emails.size(); i++) {
            assertEquals(emails.get(i).getEmail(), newPerson.getEmailAddresses().get(i).getEmail());
        }
        verify(mockJpaEmailAddress, times(emails.size())).saveAndFlush(any());
    }
}