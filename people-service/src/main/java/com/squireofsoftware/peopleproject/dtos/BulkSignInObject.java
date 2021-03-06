package com.squireofsoftware.peopleproject.dtos;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkSignInObject {
    private List<String> hashes;
    private String message;
}
