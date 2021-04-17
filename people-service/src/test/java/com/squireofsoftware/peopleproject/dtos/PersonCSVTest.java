package com.squireofsoftware.peopleproject.dtos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PersonCSVTest {
    @ParameterizedTest
    @MethodSource({"generatePeopleWithExpectedOtherNames"})
    void getOtherNames_shouldReturnEmptyList_whenOtherNamesAndEnglishAndChineseNamesAreBlank(PersonCSV personCSV,
                                                                                             List<String> expectedOtherNames) {
        // given

        // when

        // then
        assertThat(personCSV.getOtherNames()).hasSameElementsAs(expectedOtherNames);
    }

    static Stream<Arguments> generatePeopleWithExpectedOtherNames() {
        return Stream.of(
                Arguments.of(
                        PersonCSV.builder()
                            .build(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        PersonCSV.builder()
                            .givenName(UUID.randomUUID().toString())
                            .build(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .build(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("")
                                .build(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames(" ")
                                .build(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames(" ted ")
                                .build(),
                        Collections.singletonList("ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("Ted")
                                .build(),
                        Collections.singletonList("Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("人")
                                .build(),
                        Collections.singletonList("人")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames(" 人 ")
                                .build(),
                        Collections.singletonList("人")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("人|Ted")
                                .build(),
                        Arrays.asList("人", "Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("Ted|Ted")
                                .build(),
                        Collections.singletonList("Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("Ted|Ted")
                                .otherEnglishName("Test")
                                .build(),
                        Collections.singletonList("Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("Ted|Ted")
                                .otherChineseName("人")
                                .build(),
                        Collections.singletonList("Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames("Ted|Ted")
                                .otherEnglishName("Test")
                                .otherChineseName("人")
                                .build(),
                        Collections.singletonList("Ted")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherNames(null)
                                .otherEnglishName("Test")
                                .otherChineseName("人")
                                .build(),
                        Arrays.asList("Test", "人")
                ),
                Arguments.of(
                        PersonCSV.builder()
                                .givenName(UUID.randomUUID().toString())
                                .familyName(UUID.randomUUID().toString())
                                .otherEnglishName("Test")
                                .otherChineseName("人")
                                .build(),
                        Arrays.asList("Test", "人")
                )
        );
    }
}