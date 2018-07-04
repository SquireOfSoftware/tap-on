package org.squire.checkin.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "alternative_name")
public class AlternativeNameDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "alternative_name_id")
    private Integer id;
    @Column(name = "alternative_name_person_id")
    private Integer personId;
    @Column(name = "name")
    private String alternativeName;
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;
}
