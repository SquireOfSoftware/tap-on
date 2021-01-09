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

    public static PersonObject map(Person person) {
        if (person != null) {
            return PersonObject.builder()
                    .id(person.getId())
                    .familyName(person.getFamilyName())
                    .givenName(person.getGivenName())
                    .isMember(person.getIsMember())
                    .isBaptised(person.getIsBaptised())
                    .build();
        }
        return null;
    }
}
