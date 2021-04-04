package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberObject {
    @NotNull
    private String number;
    private String description;

    public static PhoneNumberObject map(PhoneNumber phoneNumber) {
        return PhoneNumberObject.builder()
                .number(phoneNumber.getNumber())
                .description(phoneNumber.getDescription())
                .build();
    }
}
