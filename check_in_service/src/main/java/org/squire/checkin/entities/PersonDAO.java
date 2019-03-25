package org.squire.checkin.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "Person")
public class PersonDAO {
    @Id
    @Column(name = "person_id")
    @GeneratedValue
    private Integer id;
    @Column(name = "given_name")
    private String givenName;
    @Column(name = "family_name")
    private String familyName;
    @Column(name = "member_since")
    private Timestamp memberSince;
    @Column(name = "baptised_since")
    private Timestamp baptisedSince;
//    @Column(name = "is_active")
//    private Boolean isActive;
}
