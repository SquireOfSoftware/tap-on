package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.entities.EmailAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddressObject {
    private String email;
    private String description;

    public static EmailAddressObject mapFrom(EmailAddress emailAddress) {
        return EmailAddressObject.builder()
                .email(emailAddress.getEmail())
                .description(emailAddress.getDescription())
                .build();
    }
}
