package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;

@Getter
@Setter
public class PersonObject {
    private Integer personId;
    private String givenName;
    private String familyName;
    @Nullable
    private Timestamp memberSince;
}
