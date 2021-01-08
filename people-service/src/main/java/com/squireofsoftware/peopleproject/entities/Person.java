package com.squireofsoftware.peopleproject.entities;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotNull
    private String givenName;
    private String familyName;
    @Column(columnDefinition = "default false")
    private Boolean isBaptised;
    @Column(columnDefinition = "default false")
    private Boolean isMember;
    private Timestamp creationDate;
    private Timestamp lastModified;

    public static Person map(PersonObject personObject) {
        if (personObject != null) {
            return Person.builder()
                    .familyName(personObject.getFamilyName())
                    .givenName(personObject.getGivenName())
                    .isBaptised(personObject.getIsBaptised())
                    .isMember(personObject.getIsMember())
                    .build();
        }
        return null;
    }
}
