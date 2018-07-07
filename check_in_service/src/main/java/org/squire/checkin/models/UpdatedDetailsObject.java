package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class UpdatedDetailsObject {
    @Nullable
    private String givenName;
    @Nullable
    private String familyName;
    @Nullable
    private Timestamp memberSince;
    @Nullable
    private Timestamp baptisedSince;
    @Nullable
    private List<AlternativeNameObject> alternativeNames;
}
