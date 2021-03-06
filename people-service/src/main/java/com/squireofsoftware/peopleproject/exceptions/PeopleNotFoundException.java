package com.squireofsoftware.peopleproject.exceptions;

import lombok.Getter;

import java.util.Set;

public class PeopleNotFoundException extends RuntimeException {
    @Getter
    private final Set<String> missingHashes;

    public PeopleNotFoundException(Set<String> missingHashes) {
        super(String.format("The following hashes could not be found: %s", missingHashes));
        this.missingHashes = missingHashes;
    }
}
