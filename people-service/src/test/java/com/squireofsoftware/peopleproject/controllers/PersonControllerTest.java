package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.EmailAddressObject;
import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PhoneNumberObject;
import com.squireofsoftware.peopleproject.entities.Language;
import com.squireofsoftware.peopleproject.services.PersonService;
import com.squireofsoftware.peopleproject.services.QrCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.squireofsoftware.peopleproject.controllers.PersonController.CSV_MEDIA_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PersonControllerTest {
    @Mock
    private PersonService mockPersonService;
    @Mock
    private QrCodeService mockQrCodeService;

    private PersonController personController;

    @BeforeEach
    public void setup() {
        openMocks(this);
        personController = new PersonController(mockPersonService, mockQrCodeService);
    }

    @Test
    void importFromCSV_throwsException_whenNonCSVFilesAreSubmitted() {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes());

        // when

        // then
        assertThrows(InvalidMediaTypeException.class,
                () -> personController.importFromCSV(dummyFile));
    }

    @Test
    void importFromCSV_returnEmptySet_whenBlankCSVFilesAreSubmitted() throws Exception {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                CSV_MEDIA_TYPE,
                "".getBytes());

        // when
        Set<PersonObject> response = personController.importFromCSV(dummyFile);

        // then
        assertEquals(Collections.EMPTY_SET, response);
    }

    @Test
    void importFromCSV_returnSet_whenCSVFilesAPersonIsSubmitted() throws Exception {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                CSV_MEDIA_TYPE,
                ("given_name,family_name,member,baptised,phone_numbers,email_addresses\n" +
                        "John,Smith,TRUE,TRUE,0400 000 000,test@test.com").getBytes());

        PersonObject expectedPerson = PersonObject.builder()
                .givenName("John")
                .familyName("Smith")
                .isBaptised(true)
                .isMember(true)
                .phoneNumbers(Collections.singletonList(PhoneNumberObject.builder()
                        .number("0400 000 000")
                        .build()))
                .emailAddresses(Collections.singletonList(EmailAddressObject.builder()
                        .email("test@test.com")
                        .build()))
                .build();

        when(mockPersonService.createPerson(eq(expectedPerson)))
                .thenReturn(expectedPerson);

        // when
        Set<PersonObject> response = personController.importFromCSV(dummyFile);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());

        verify(mockPersonService, times(1))
                .createPerson(eq(expectedPerson));
    }

    @Test
    void importFromCSV_returnSet_whenCSVFilesAPersonWithOtherNamesIsSubmitted() throws Exception {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                CSV_MEDIA_TYPE,
                ("given_name,family_name,member,baptised,phone_numbers,email_addresses,other_names\n" +
                        "John,Smith,TRUE,TRUE,0400 000 000,test@test.com,人|Johnny").getBytes());

        PersonObject expectedPerson = PersonObject.builder()
                .givenName("John")
                .familyName("Smith")
                .isBaptised(true)
                .isMember(true)
                .phoneNumbers(Collections.singletonList(PhoneNumberObject.builder()
                        .number("0400 000 000")
                        .build()))
                .emailAddresses(Collections.singletonList(EmailAddressObject.builder()
                        .email("test@test.com")
                        .build()))
                .otherNames(Arrays.asList(
                        NameObject.builder()
                                .name("人")
                                .language(Language.Chinese.name())
                                .build(),
                        NameObject.builder()
                                .name("Johnny")
                                .language(Language.English.name())
                                .build()))
                .build();

        when(mockPersonService.createPerson(eq(expectedPerson)))
                .thenReturn(expectedPerson);

        // when
        Set<PersonObject> response = personController.importFromCSV(dummyFile);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());

        ArgumentCaptor<PersonObject> csvPersonCaptor = ArgumentCaptor.forClass(PersonObject.class);
        verify(mockPersonService, times(1))
                .createPerson(csvPersonCaptor.capture());
        assertEquals(expectedPerson.getGivenName(), csvPersonCaptor.getValue().getGivenName());
        assertEquals(expectedPerson.getFamilyName(), csvPersonCaptor.getValue().getFamilyName());

        assertEquals(expectedPerson.getIsBaptised(), csvPersonCaptor.getValue().getIsBaptised());
        assertEquals(expectedPerson.getIsMember(), csvPersonCaptor.getValue().getIsMember());

        assertThat(csvPersonCaptor.getValue().getEmailAddresses())
                .hasSameElementsAs(expectedPerson.getEmailAddresses());
        assertThat(csvPersonCaptor.getValue().getPhoneNumbers())
                .hasSameElementsAs(expectedPerson.getPhoneNumbers());
        assertThat(csvPersonCaptor.getValue().getOtherNames())
                .hasSameElementsAs(expectedPerson.getOtherNames());
    }

    @Test
    void importFromCSV_returnSet_whenCSVFilesAPersonWithChineseNameIsSubmitted() throws Exception {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                CSV_MEDIA_TYPE,
                ("given_name,family_name,member,baptised,phone_numbers,email_addresses,other_english_name,other_chinese_name\n" +
                        "John,Smith,TRUE,TRUE,0400 000 000,test@test.com,,人").getBytes());

        PersonObject expectedPerson = PersonObject.builder()
                .givenName("John")
                .familyName("Smith")
                .isBaptised(true)
                .isMember(true)
                .phoneNumbers(Collections.singletonList(PhoneNumberObject.builder()
                        .number("0400 000 000")
                        .build()))
                .emailAddresses(Collections.singletonList(EmailAddressObject.builder()
                        .email("test@test.com")
                        .build()))
                .otherNames(Collections.singletonList(NameObject.builder()
                        .name("人")
                        .language(Language.Chinese.name())
                        .build()))
                .build();

        when(mockPersonService.createPerson(eq(expectedPerson)))
                .thenReturn(expectedPerson);

        // when
        Set<PersonObject> response = personController.importFromCSV(dummyFile);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());

        verify(mockPersonService, times(1))
                .createPerson(eq(expectedPerson));
    }

    @Test
    void importFromCSV_returnSet_whenCSVFilesAPersonWithMultipleEmailsMultiplePhoneNumbersAndOverridingOtherNamesIsSubmitted() throws Exception {
        // given
        MultipartFile dummyFile = new MockMultipartFile("test",
                "something",
                CSV_MEDIA_TYPE,
                ("given_name,family_name,member,baptised,phone_numbers,email_addresses,other_names,other_english_name,other_chinese_name\n" +
                        "John,Smith,TRUE,TRUE,0400 000 000| 123 123 123 ,test@test.com | other_email@test.com ,some| other| name ,,人").getBytes());

        PersonObject expectedPerson = PersonObject.builder()
                .givenName("John")
                .familyName("Smith")
                .isBaptised(true)
                .isMember(true)
                .phoneNumbers(Arrays.asList(
                        PhoneNumberObject.builder()
                                .number("0400 000 000")
                                .build(),
                        PhoneNumberObject.builder()
                                .number("123 123 123")
                                .build()))
                .emailAddresses(Arrays.asList(
                        EmailAddressObject.builder()
                                .email("test@test.com")
                                .build(),
                        EmailAddressObject.builder()
                                .email("other_email@test.com")
                                .build()))
                .otherNames(Arrays.asList(
                        NameObject.builder()
                                .name("some")
                                .language(Language.English.name())
                                .build(),
                        NameObject.builder()
                                .name("other")
                                .language(Language.English.name())
                                .build(),
                        NameObject.builder()
                                .name("name")
                                .language(Language.English.name())
                                .build()))
                .build();

        when(mockPersonService.createPerson(eq(expectedPerson)))
                .thenReturn(expectedPerson);

        // when
        Set<PersonObject> response = personController.importFromCSV(dummyFile);

        // then
        assertNotNull(response);
        assertEquals(1, response.size());

        verify(mockPersonService, times(1))
                .createPerson(eq(expectedPerson));
    }
}