package com.squireofsoftware.peopleproject.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("This person was not found");
    }

    public PersonNotFoundException(Integer personId) {
        super(String.format("The person with the id of; %d could not be found", personId));
    }
}
