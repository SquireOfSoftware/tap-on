package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_servicecomponent")
public class ServiceComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ServiceComponentType type;
    @OneToMany(targetEntity = Person.class)
    private Set<Person> people;
}
