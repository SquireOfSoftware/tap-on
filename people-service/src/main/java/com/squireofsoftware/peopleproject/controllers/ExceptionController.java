package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.exceptions.ExceptionMessage;
import com.squireofsoftware.peopleproject.exceptions.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {
    @ResponseBody
    @ExceptionHandler({PersonNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionMessage processNotFound(RuntimeException e) {
        return ExceptionMessage.builder().message(e.getMessage()).build();
    }
}
