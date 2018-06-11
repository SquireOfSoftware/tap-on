package org.squire.checkin.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Getter
@Setter
@Entity
@Table(name = "alternative_name")
public class AlternativeNameDAO {
    @Id
    @GeneratedValue
    @Column(name = "person_id")
    private Integer id;
    @Column(name = "alternative_name_person_id")
    private Integer personId;
    @Column(name = "name")
    private String alternativeName;
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;
}
