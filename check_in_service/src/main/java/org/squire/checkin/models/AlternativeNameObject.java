package org.squire.checkin.models;

import lombok.Getter;
import lombok.Setter;
import org.squire.checkin.entities.Language;

@Getter
@Setter
public class AlternativeNameObject {
    private String alternativeName;
    private Language language;
    // assume there is one alternative name per language
}
