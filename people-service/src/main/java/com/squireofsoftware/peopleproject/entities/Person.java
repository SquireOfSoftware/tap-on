package com.squireofsoftware.peopleproject.entities;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
}
