package com.squireofsoftware.peopleproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_checkinlog")
public class CheckinLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @NotNull
    private Timestamp timestamp;
    private String message;
}
