package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PersonObject {
    private Integer personId;
    private String givenName;
    private String familyName;
    @Nullable
    private Timestamp memberSince;
    @Nullable
    private Timestamp baptisedSince;
    private Timestamp lastSignIn;
    private List<AlternativeNameObject> alternativeNames;
}
