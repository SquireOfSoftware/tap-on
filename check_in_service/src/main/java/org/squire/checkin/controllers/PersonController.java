package org.squire.checkin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.UpdatedDetailsObject;
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

    @PutMapping(value = BASE_PATH + "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updatePerson(@PathVariable Integer id, @RequestBody UpdatedDetailsObject updatedDetailsObject){
        if (personService.hasPerson(id)) {
            boolean hasUpdated = personService.updatePerson(id, updatedDetailsObject);
            return hasUpdated ? ResponseEntity.ok(true): ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = BASE_PATH + "/{id}")
    public ResponseEntity getPerson(@PathVariable Integer id) {
        if (personService.hasPerson(id)) {
            return ResponseEntity.ok(personService.getPerson(id));
        }
        return ResponseEntity.notFound().build();
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

    /**
     * Example:
     * {
     * 		"personId": 1,
     * 		"signInTime": "2018-07-04T10:44:57+00:00"
     * }
     */
    @PostMapping(value = BASE_PATH + "/signin", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signInPersons(@RequestBody List<SignInObject> signInObjects) {
        return ResponseEntity.ok(signInTimeService.addSignIns(signInObjects));
    }
}
