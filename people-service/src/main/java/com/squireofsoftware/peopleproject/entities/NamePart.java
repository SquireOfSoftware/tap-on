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
@Table(name = "t_namepart")
public class NamePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String value;
    @Enumerated(EnumType.STRING)
    private Language type;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;
}
