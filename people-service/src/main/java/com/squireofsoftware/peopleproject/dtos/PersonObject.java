package com.squireofsoftware.peopleproject.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squireofsoftware.peopleproject.entities.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonObject {
    private Integer id;
    @NotNull
    private String givenName;
    private String familyName;
    @Builder.Default
    private List<NameObject> otherNames = Collections.emptyList();
    @Builder.Default
    private Boolean isBaptised = false;
    @Builder.Default
    private Boolean isMember = false;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer hash;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EmailAddressObject> emailAddresses = Collections.emptyList();
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PhoneNumberObject> phoneNumbers = Collections.emptyList();

    public static PersonObject map(Person person) {
        if (person != null) {
            return PersonObject.builder()
                    .id(person.getId())
                    .familyName(person.getFamilyName())
                    .givenName(person.getGivenName())
                    .isMember(person.getIsMember())
                    .isBaptised(person.getIsBaptised())
                    .otherNames(person.getAlternativeNames()
                            .stream()
                            .map(NameObject::mapFrom)
                            .collect(Collectors.toList()))
                    .phoneNumbers(person.getPhoneNumbers().stream()
                            .map(PhoneNumberObject::mapFrom)
                            .collect(Collectors.toList()))
                    .emailAddresses(person.getEmailAddresses().stream()
                            .map(EmailAddressObject::mapFrom)
                            .collect(Collectors.toList()))
                    .build();
        }
        return null;
    }
}
