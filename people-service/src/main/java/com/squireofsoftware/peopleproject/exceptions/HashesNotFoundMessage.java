package com.squireofsoftware.peopleproject.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HashesNotFoundMessage extends ExceptionMessage {
    @Singular
    private Set<String> missingHashes;
}
