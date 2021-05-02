package com.squireofsoftware.peopleproject.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squireofsoftware.peopleproject.entities.Person;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonObject extends RepresentationModel<PersonObject>
                            implements PersonInterface<PersonObject> {
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
    @Builder.Default
    private Boolean isVisitor = false;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String hash;

    private LocalDateTime lastModified;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<EmailAddressObject> emailAddresses = Collections.emptyList();
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PhoneNumberObject> phoneNumbers = Collections.emptyList();

    public static PersonObject map(Person person) {
        if (person != null) {
            PersonObject personObject = PersonObject.builder()
                    .id(person.getId())
                    .familyName(person.getFamilyName())
                    .givenName(person.getGivenName())
                    .isMember(person.getIsMember())
                    .isBaptised(person.getIsBaptised())
                    .isVisitor(person.getIsVisitor())
                    .hash(person.getHash())
                    .otherNames(person.getOtherNames()
                            .stream()
                            .map(NameObject::map)
                            .collect(Collectors.toList()))
                    .phoneNumbers(person.getPhoneNumbers().stream()
                            .map(PhoneNumberObject::map)
                            .collect(Collectors.toList()))
                    .emailAddresses(person.getEmailAddresses().stream()
                            .map(EmailAddressObject::map)
                            .collect(Collectors.toList()))
                    .lastModified(person.getLastModified() != null ?
                            person.getLastModified()
                                    .toLocalDateTime() :
                            null)
                    .build();

            personObject.addLinks();

            return personObject;
        }
        return null;
    }

    /**
     * This should not add any links because it has not enough information
     * from the CSV to generate the required links.
     * Please use with caution.
     */
    public static PersonObject map(PersonCSV personCSV) {
        if (personCSV != null) {
            return PersonObject.builder()
                    .familyName(personCSV.getFamilyName())
                    .givenName(personCSV.getGivenName())
                    .isBaptised(personCSV.isBaptised())
                    .isMember(personCSV.isMember())
                    .isVisitor(personCSV.isVisitor())
                    .emailAddresses(personCSV.getEmailAddresses().stream()
                            .map(email -> EmailAddressObject.builder()
                                    .email(email)
                                    .build()
                            )
                            .collect(Collectors.toList())
                    )
                    .phoneNumbers(personCSV.getPhoneNumbers().stream()
                            .map(phone -> PhoneNumberObject.builder()
                                    .number(phone)
                                    .build()
                            )
                            .collect(Collectors.toList())
                    )
                    .otherNames(personCSV.getOtherNames().stream()
                            .map(name -> {
                                String language = "English";
                                if (Character.UnicodeBlock.of(name.charAt(0)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                                    language = "Chinese";
                                }
                                return NameObject.builder()
                                                .name(name)
                                                .language(language)
                                                .build();
                                }
                            )
                            .collect(Collectors.toList())
                    )
                    .build();
        }
        return null;
    }
}
