package org.squire.checkin.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "sign_in")
public class SignInDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_in_id")
    private Integer id;
    @Column(name = "time")
    private Timestamp signInTime;
    @Column(name = "sign_in_person_id")
    private Integer personId;
}
