package com.squireofsoftware.peopleproject.dtos;

import com.squireofsoftware.peopleproject.controllers.CheckinController;
import com.squireofsoftware.peopleproject.controllers.PersonController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public interface PersonInterface<T> {
    Integer getId();
    String getHash();
    T add(Link link);

    private void addSelfReference() {
        if (getId() != null) {
            add(linkTo(PersonController.class)
                    .slash("id")
                    .slash(getId())
                    .withSelfRel());
        } else {
            throw new NullPointerException("Self rel link id cannot be null");
        }
    }

    private void addSignInReference() {
        if (StringUtils.isNotBlank(getHash())) {
            add(linkTo(CheckinController.class)
                    .slash("signin")
                    .slash(getHash())
                    .withRel("Sign in post request"));
        } else {
            throw new NullPointerException("Sign in hash rel link cannot be null");
        }
    }

    private void addLogReference() {
        if (StringUtils.isNotBlank(getHash())) {
            add(linkTo(CheckinController.class)
                    .slash("people")
                    .slash("log")
                    .slash("hash")
                    .slash(getHash())
                    .withRel("All sign in logs for person"));
        } else {
            throw new NullPointerException("Sign in hash rel link cannot be null");
        }
    }

    default void addLinks() {
        addSelfReference();
        addSignInReference();
        addLogReference();
    }
}
