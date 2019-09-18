package com.squireofsoftware.peopleproject.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_namepart")
public class NamePart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String value;
    @Enumerated(EnumType.STRING)
    private Language type;
    private Integer personId;
}
