package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.Language;
import com.squireofsoftware.peopleproject.entities.NamePart;
import com.squireofsoftware.peopleproject.entities.Person;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PersonReferenceObjectTest {
    @Test
    public void map_shouldBuildWithEmptyList_whenNoAlternativeNamesAreProvided() {
        // given
        Person dummyPerson = Person.builder()
                .givenName("test")
                .id(1)
                .hash(UUID.randomUUID().toString())
                .build();

        // when
        PersonReferenceObject result = PersonReferenceObject.map(dummyPerson);

        // then
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.getOtherNames());
    }

    @Test
    public void map_shouldBuildWithAlternativeNames_whenAlternativeNamesAreProvided() {
        // given
        String alternativeName = "another name";

        Person dummyPerson = Person.builder()
                .givenName("test")
                .id(1)
                .hash(UUID.randomUUID().toString())
                .otherNames(Collections.singletonList(
                        NamePart.builder()
                                .value(alternativeName)
                                .type(Language.English)
                                .build()))
                .build();

        // when
        PersonReferenceObject result = PersonReferenceObject.map(dummyPerson);

        // then
        assertNotNull(result);
        assertEquals(1, result.getOtherNames().size());
        assertEquals(alternativeName, result.getOtherNames().get(0).getName());
    }
}