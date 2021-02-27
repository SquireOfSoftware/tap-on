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
@Table(name = "t_phonenumber")
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String number;
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "personId")
    private Person person;
}
