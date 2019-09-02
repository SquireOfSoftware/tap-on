package com.squireofsoftware.peopleproject.entities;

import javax.persistence.*;

@Entity
@Table(name = "t_namepart")
public class NamePart {
    @Id
    private Integer id;
    private String value;
    @Enumerated(EnumType.STRING)
    private Language type;
}
