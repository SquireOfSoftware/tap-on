package org.squire.checkin.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class AlternativeName {
    @Id
    private Integer id;
    @Column(name = "person_id")
    private Integer personId;
    @Column(name = "alternative_name")
    private String alternativeName;
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;
}
