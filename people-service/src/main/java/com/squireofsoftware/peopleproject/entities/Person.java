package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collections;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String givenName;
    private String familyName;
    @Column(columnDefinition = "bool default false")
    private Boolean isBaptised;
    @Column(columnDefinition = "bool default false")
    private Boolean isMember;
    private Timestamp creationDate;
    private Timestamp lastModified;
    @NotNull
    private String hash;

    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    @Builder.Default
    private List<PhoneNumber> phoneNumbers = Collections.emptyList();
    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    @Builder.Default
    private List<EmailAddress> emailAddresses = Collections.emptyList();
    @NotNull
    @OneToMany
    @JoinColumn(name = "personId")
    @Builder.Default
    private List<NamePart> alternativeNames = Collections.emptyList();
}
