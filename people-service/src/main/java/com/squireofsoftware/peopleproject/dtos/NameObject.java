package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.NamePart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NameObject {
    private String name;
    private String language;

    public static NameObject mapFrom(NamePart namePart) {
        return NameObject.builder()
                .name(namePart.getValue())
                .language(namePart.getType().name())
                .build();
    }
}
