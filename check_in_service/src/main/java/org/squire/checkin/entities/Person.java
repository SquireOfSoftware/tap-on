package org.squire.checkin.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Person {
    @Id
    private Integer id;
    @Column(name = "given_name")
    private String givenName;
    @Column(name = "family_name")
    private String familyName;
}
