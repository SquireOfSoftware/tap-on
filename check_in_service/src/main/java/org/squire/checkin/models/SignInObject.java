package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SignInObject {
    private Integer personId;
    private Timestamp signInTime;
}
