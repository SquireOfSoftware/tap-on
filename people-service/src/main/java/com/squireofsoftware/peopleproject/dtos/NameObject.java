package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.NamePart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NameObject {
    @NotNull
    private String name;
    @NotNull
    private String language;

    public static NameObject map(NamePart namePart) {
        return NameObject.builder()
                .name(namePart.getValue())
                .language(namePart.getType().name())
                .build();
    }
}
