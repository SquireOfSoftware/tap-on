package com.squireofsoftware.peopleproject.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonCSV {
    @CsvBindByName(column = "GivenName")
    @Getter
    private String givenName;
    @CsvBindByName(column = "FamilyName")
    @Getter
    private String familyName;
    @CsvBindByName(column = "Member")
    @Getter
    private boolean member;
    @CsvBindByName(column = "Baptised")
    @Getter
    private boolean baptised;

    @CsvBindByName(column = "EmailAddresses")
    private String emailAddresses;
    @CsvBindByName(column = "PhoneNumbers")
    private String phoneNumbers;
    @CsvBindByName(column = "AlternativeNames")
    private String alternativeNames;

    public List<String> getEmailAddresses() {
        return StringUtils.isNotBlank(emailAddresses) ?
                Arrays.asList(emailAddresses.split("\\|")) :
                Collections.emptyList();
    }

    public List<String> getPhoneNumbers() {
        return StringUtils.isNotBlank(phoneNumbers) ?
                Arrays.asList(phoneNumbers.split("\\|")) :
                Collections.emptyList();
    }

    public List<String> getAlternativeNames() {
        return StringUtils.isNotBlank(alternativeNames) ?
                Arrays.asList(alternativeNames.split("\\|")) :
                Collections.emptyList();
    }
}
