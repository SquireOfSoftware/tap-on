package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class SignOutObject {
    private Integer personId;
    private Timestamp beforeDate;
    private Timestamp afterDate;
}
