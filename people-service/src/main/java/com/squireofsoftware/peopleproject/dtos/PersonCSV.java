package com.squireofsoftware.peopleproject.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonCSV {
    @CsvBindByName(column = "given_name")
    private String givenName;
    @CsvBindByName(column = "family_name")
    private String familyName;
    @CsvBindByName(column = "member")
    @Getter
    private boolean member;
    @CsvBindByName(column = "baptised")
    @Getter
    private boolean baptised;
    @CsvBindByName(column = "visitor")
    @Getter
    private boolean visitor;

    @CsvBindByName(column = "email_addresses")
    private String emailAddresses;
    @CsvBindByName(column = "phone_numbers")
    private String phoneNumbers;
    @CsvBindByName(column = "other_names")
    private String otherNames;

    @CsvBindByName(column = "other_english_name")
    private String otherEnglishName;
    @CsvBindByName(column = "other_chinese_name")
    private String otherChineseName;

    public String getGivenName() {
        return trimString(givenName);
    }

    public String getFamilyName() {
        return trimString(familyName);
    }

    private String trimString(String input) {
        return input != null ? input.trim() : null;
    }

    public List<String> getEmailAddresses() {
        List<String> emails = StringUtils.isNotBlank(emailAddresses) ?
                Arrays.asList(emailAddresses.split("\\|")) :
                Collections.emptyList();
        return emails.stream()
                .map(this::trimString)
                .collect(Collectors.toList());
    }

    public List<String> getPhoneNumbers() {
        List<String> numbers = StringUtils.isNotBlank(phoneNumbers) ?
                Arrays.asList(phoneNumbers.split("\\|")) :
                Collections.emptyList();
        return numbers.stream()
                .map(this::trimString)
                .collect(Collectors.toList());
    }

    public List<String> getOtherNames() {
        List<String> splitOtherNames = StringUtils.isNotBlank(otherNames) ?
                Stream.of(otherNames.split("\\|"))
                        .map(String::trim)
                        .collect(Collectors.toList()) :
                null;

        Set<String> names =
                splitOtherNames != null ?
                new HashSet<>(splitOtherNames) :
                new HashSet<>();
        if (names.size() == 0) {
            // check the english and chinese name column
            if (StringUtils.isNotBlank(trimString(otherEnglishName))) {
                names.add(trimString(otherEnglishName));
            }
            if (StringUtils.isNotBlank(trimString(otherChineseName))) {
                names.add(trimString(otherChineseName));
            }
        }
        return new ArrayList<>(names);
    }
}
