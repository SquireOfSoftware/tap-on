package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

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
    @NotNull
    private Integer hash;

    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    private List<PhoneNumber> phoneNumbers;
    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    private List<EmailAddress> emailAddresses;
    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    private List<NamePart> alternativeNames;
}
