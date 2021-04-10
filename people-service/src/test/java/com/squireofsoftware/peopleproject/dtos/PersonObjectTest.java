package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.Language;
import com.squireofsoftware.peopleproject.entities.Person;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonObjectTest {
    @Test
    public void map_shouldReturnNull_whenInputIsNull() {
        // given

        // when

        // then
        assertNull(PersonObject.map((Person) null));
    }

    @Test
    public void map_shouldThrowException_whenIdIsNull() {
        // given
        Person dummyPerson = Person.builder()
                .givenName("test")
                .familyName("test")
                .hash("hash123")
                .build();

        // when - then
        assertThrows(NullPointerException.class, () -> PersonObject.map(dummyPerson));
    }

    @Test
    public void map_shouldThrowException_whenHashIsNull() {
        // given
        Person dummyPerson = Person.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .build();

        // when - then
        assertThrows(NullPointerException.class, () -> PersonObject.map(dummyPerson));
    }

    @Test
    public void map_shouldAddSelfRelLink_whenIdIsNotNull() {
        // given
        Person dummyPerson = Person.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .hash("hash123")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPerson);

        // then
        assertTrue(result.getLink(PersonInterface.SELF_REL).isPresent());
        Link selfLink = result.getLink(PersonInterface.SELF_REL).get();
        assertEquals("/people/id/1", selfLink.getHref());
    }

    @Test
    public void map_shouldAddSignInLink_whenHashIsNotBlank() {
        // given
        Person dummyPerson = Person.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .hash("hash123")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPerson);

        // then
        assertTrue(result.getLink(PersonInterface.SIGN_IN_REL).isPresent());
        Link selfLink = result.getLink(PersonInterface.SIGN_IN_REL).get();
        assertEquals("/checkin/signin/hash/hash123", selfLink.getHref());
    }

    @Test
    public void map_shouldAddQrLink_whenIdIsNotNull() {
        // given
        Person dummyPerson = Person.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .hash("hash123")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPerson);

        // then
        assertTrue(result.getLink(PersonInterface.QR_REL).isPresent());
        Link selfLink = result.getLink(PersonInterface.QR_REL).get();
        assertEquals("/people/id/1/qrcode", selfLink.getHref());
    }

    @Test
    public void map_shouldAddCheckinLogLink_whenHashIsNotBlank() {
        // given
        Person dummyPerson = Person.builder()
                .id(1)
                .givenName("test")
                .familyName("test")
                .hash("hash123")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPerson);

        // then
        assertTrue(result.getLink(PersonInterface.LOG_REL).isPresent());
        Link selfLink = result.getLink(PersonInterface.LOG_REL).get();
        assertEquals("/checkin/people/log/hash/hash123", selfLink.getHref());
    }

    @Test
    public void mapCSV_shouldReturnNull_whenInputIsNull() {
        // given

        // when

        // then
        assertNull(PersonObject.map((PersonCSV) null));
    }

    @Test
    public void mapCSV_shouldReturnBlankEmails_whenNoEmailsProvided() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.getEmailAddresses());
    }

    @Test
    public void mapCSV_shouldReturnBlankPhoneNumbers_whenNoPhoneNumbersProvided() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.getPhoneNumbers());
    }

    @Test
    public void mapCSV_shouldReturnBlankAlternativeNames_whenNoOtherNamesProvided() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .build();

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.getOtherNames());
    }

    @Test
    public void mapCSV_shouldSplitEmailsAccordingToPipeCharacter() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .emailAddresses("test@test.org|bad_email|something@www.com")
                .build();

        List<EmailAddressObject> expectedEmails = Arrays.asList(
                EmailAddressObject.builder()
                        .email("test@test.org")
                        .build(),
                EmailAddressObject.builder()
                        .email("bad_email")
                        .build(),
                EmailAddressObject.builder()
                        .email("something@www.com")
                        .build()
        );

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(expectedEmails, result.getEmailAddresses());
    }

    @Test
    public void mapCSV_shouldSplitPhoneNumbersAccordingToPipeCharacter() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .phoneNumbers("+6123|+2312|9813")
                .build();

        List<PhoneNumberObject> expectedNumbers = Arrays.asList(
                PhoneNumberObject.builder()
                        .number("+6123")
                        .build(),
                PhoneNumberObject.builder()
                        .number("+2312")
                        .build(),
                PhoneNumberObject.builder()
                        .number("9813")
                        .build()
        );

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(expectedNumbers, result.getPhoneNumbers());
    }

    @Test
    public void mapCSV_shouldSplitAlternativeNamesAccordingToPipeCharacter() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .otherNames("Test|English|Chinese")
                .build();

        List<NameObject> expectedNames = Arrays.asList(
                NameObject.builder()
                        .name("Test")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("English")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("Chinese")
                        .language(Language.English.name())
                        .build()
        );

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(expectedNames, result.getOtherNames());
    }

    @Test
    public void mapCSV_shouldAutoFillEnglishAsDefaultOtherNameLanguage_whenLanguageNotProvided() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .otherNames("Test|English|Chinese")
                .build();

        List<NameObject> expectedNames = Arrays.asList(
                NameObject.builder()
                        .name("Test")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("English")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("Chinese")
                        .language(Language.English.name())
                        .build()
        );

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(expectedNames, result.getOtherNames());
    }

    @Test
    public void mapCSV_shouldRecogniseChineseAsOtherNameLanguage_whenLanguageNotProvided_andChineseInputIsGiven() {
        // given
        PersonCSV dummyPersonCSV = PersonCSV.builder()
                .givenName("test")
                .familyName("test")
                .otherNames("Test|English|人")
                .build();

        List<NameObject> expectedNames = Arrays.asList(
                NameObject.builder()
                        .name("Test")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("English")
                        .language(Language.English.name())
                        .build(),
                NameObject.builder()
                        .name("人")
                        .language(Language.Chinese.name())
                        .build()
        );

        // when
        PersonObject result = PersonObject.map(dummyPersonCSV);

        // then
        assertNotNull(result);
        assertEquals(expectedNames, result.getOtherNames());
    }
}