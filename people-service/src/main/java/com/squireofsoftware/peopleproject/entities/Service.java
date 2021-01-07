package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(unique=true)
    private Date time;
    @OneToMany(targetEntity = ServiceComponent.class)
    private Set<ServiceComponent> components;
}