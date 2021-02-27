package com.squireofsoftware.peopleproject.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squireofsoftware.peopleproject.controllers.PersonController;
import com.squireofsoftware.peopleproject.entities.Person;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonReferenceObject extends RepresentationModel<PersonObject> {
    private Integer id;
    @NotNull
    private String givenName;
    private String familyName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String hash;

    public static PersonReferenceObject from(Person person) {
        if (person != null) {
            PersonReferenceObject personReference = PersonReferenceObject.builder()
                    .id(person.getId())
                    .familyName(person.getFamilyName())
                    .givenName(person.getGivenName())
                    .hash(person.getHash())
                    .build();

            personReference.add(
                    linkTo(PersonController.class)
                            .slash(person.getId())
                            .withSelfRel());

            return personReference;
        }
        return null;
    }
}
