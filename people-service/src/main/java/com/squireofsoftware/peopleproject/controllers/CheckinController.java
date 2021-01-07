package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.services.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkin")
public class CheckinController {
    @Autowired
    private HashService hashService;

    @GetMapping(value = "/hash/{hash}")
    public PersonObject findPersonByHash(@PathVariable Integer hash) {
        return hashService.getPerson(hash);
    }

    @GetMapping(value = "/person/hash/{personId}")
    public Integer getHashByPersonId(@PathVariable Integer personId) {
        return hashService.getHash(personId);
    }

    @PostMapping(value = "/signin")
    public void test() {

    }
}
