package com.squireofsoftware.peopleproject.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("This person was not found");
    }
}
