package com.squireofsoftware.peopleproject.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squireofsoftware.peopleproject.entities.Person;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonReferenceObject extends RepresentationModel<PersonObject>
                                    implements PersonInterface<PersonObject> {
    private Integer id;
    @NotNull
    private String givenName;
    private String familyName;
    private List<NameObject> otherNames;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String hash;

    public static PersonReferenceObject map(Person person) {
        if (person != null) {
            List<NameObject> otherNames = person.getOtherNames()
                    .stream()
                    .map(NameObject::map)
                    .collect(Collectors.toList());

            PersonReferenceObject personReference = PersonReferenceObject.builder()
                    .id(person.getId())
                    .familyName(person.getFamilyName())
                    .givenName(person.getGivenName())
                    .otherNames(otherNames)
                    .hash(person.getHash())
                    .build();

            personReference.addLinks();

            return personReference;
        }
        return null;
    }
}
