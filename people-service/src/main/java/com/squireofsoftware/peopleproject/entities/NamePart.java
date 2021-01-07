package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
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
    @JoinColumn(table = "t_person")
    private Integer personId;
}
