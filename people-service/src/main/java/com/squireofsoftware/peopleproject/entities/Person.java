package com.squireofsoftware.peopleproject.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_person")
public class Person {
    @Id
    private Integer id;
    @OneToOne
    private NamePart givenName;
    @OneToOne
    private NamePart familyName;
    @Transient
    private List<NamePart> otherNames;
    private Boolean isBaptised;
    private Boolean isMember;
}
