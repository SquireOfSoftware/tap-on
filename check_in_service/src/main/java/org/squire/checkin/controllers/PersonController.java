package org.squire.checkin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.squire.checkin.models.MessageObject;
import org.squire.checkin.models.PersonObject;
import org.squire.checkin.models.SignInObject;
import org.squire.checkin.models.SignOutObject;
import org.squire.checkin.models.UpdatedDetailsObject;
import org.squire.checkin.services.PersonService;
import org.squire.checkin.services.SignInTimeService;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@CrossOrigin(origins = {"http://localhost:8080", "*"})
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

    @PostMapping(value = BASE_PATH, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createPerson(@RequestBody PersonObject personObject) {
        return ResponseEntity.ok(personService.createPerson(personObject));
    }

    @PutMapping(value = BASE_PATH + "/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updatePerson(@PathVariable Integer id, @RequestBody UpdatedDetailsObject updatedDetailsObject){
        if (personService.hasPerson(id)) {
            MessageObject hasUpdated = personService.updatePerson(id, updatedDetailsObject);
            return hasUpdated.isSuccessful() ? ResponseEntity.ok(hasUpdated): ResponseEntity.unprocessableEntity().body(hasUpdated);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = BASE_PATH + "/{id}")
    public ResponseEntity getPerson(@PathVariable Integer id) {
        PersonObject personObject = personService.getPerson(id);
        return personObject != null ?
                ResponseEntity.ok(personObject):
                ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = BASE_PATH + "/{id}")
    public ResponseEntity deletePerson(@PathVariable Integer id) {
        if (personService.removePerson(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = BASE_PATH + "/{id}/latestSignIn")
    public ResponseEntity getSignIn(@PathVariable Integer id) {
        SignInObject signIn = signInTimeService.getLatestSignIn(id);
        return signIn == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(signIn);
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
    @PostMapping(value = BASE_PATH + "/signin", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signInPersons(@RequestBody List<SignInObject> signInObjects) {
        return ResponseEntity.ok(signInTimeService.addSignIns(signInObjects));
    }

    @PostMapping(value = BASE_PATH + "/{id}/signin")
    public ResponseEntity signInPersons(@PathVariable Integer id) {
        return ResponseEntity.ok(signInTimeService.signInPersonId(id));
    }

    @PostMapping(value = BASE_PATH + "/signout", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity signOutPersons(@RequestBody List<SignOutObject> signOutObjects) {
        return ResponseEntity.ok(signInTimeService.removeSignIns(signOutObjects));
    }

    //https://stackoverflow.com/questions/37307697/scheduled-websocket-push-with-springboot
    @GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test() {
        return Flux.fromStream(Stream.of("1", "2", "3", "4"));
    }
}
