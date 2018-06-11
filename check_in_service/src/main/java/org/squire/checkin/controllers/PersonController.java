package org.squire.checkin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.services.PersonService;
import org.squire.checkin.services.SignInTimeService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class PersonController {
    private PersonService personService;
    private SignInTimeService signInTimeService;

    private static final String BASE_PATH = "/persons";

    @Autowired
    public PersonController(PersonService personService, SignInTimeService signInTimeService) {
        this.personService = personService;
        this.signInTimeService = signInTimeService;
    }

    @GetMapping(value = BASE_PATH)
    public List<PersonObject> getAllPeople() {
        return personService.getAllPeople();
    }

    @PostMapping(value = BASE_PATH, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createPerson(@RequestBody PersonObject personObject) {
        return ResponseEntity.ok(personService.createPerson(personObject));
    }

    @DeleteMapping(value = BASE_PATH + "/{id}")
    public ResponseEntity deletePerson(@PathVariable Integer id) {
        if (personService.removePerson(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = BASE_PATH + "/members")
    public List<PersonObject> getAllMembers() {
        return personService.getAllMembers();
    }

    @PostMapping(value = BASE_PATH + "/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signInPersons(@RequestBody List<SignInObject> signInObjects) {
        return ResponseEntity.ok(signInTimeService.addSignIns(signInObjects));
    }
}
