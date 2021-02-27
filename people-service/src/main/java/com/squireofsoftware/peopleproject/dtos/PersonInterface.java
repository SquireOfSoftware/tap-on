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
        add(linkTo(PersonController.class)
                .slash("id")
                .slash(getId())
                .withSelfRel());
    }

    private void addSignInReference() {
        add(linkTo(CheckinController.class)
                .slash("signin")
                .slash(getHash())
                .withRel("Sign in post request"));
    }

    private void addLogReference() {
        add(linkTo(CheckinController.class)
                .slash("people")
                .slash("log")
                .slash("hash")
                .slash(getHash())
                .withRel("All sign in logs for person"));
    }

    private void addQrCodeReference() {
        add(linkTo(PersonController.class)
                .slash("id")
                .slash(getId())
                .slash("qrcode")
                .withRel("Qr code for person"));
    }

    default void addLinks() {
        if (getId() == null) {
            throw new NullPointerException("Self rel link id cannot be null");
        } else if (StringUtils.isBlank(getHash())) {
            throw new NullPointerException("Sign in rel link hash cannot be null");
        }

        addSelfReference();
        addSignInReference();
        addLogReference();
        addQrCodeReference();
    }
}
