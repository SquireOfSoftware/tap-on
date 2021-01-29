package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/people")
@CrossOrigin
public class PersonController {
    @Autowired
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping(value = "/id/{id}")
    public PersonObject getPerson(@PathVariable Integer id) {
        return personService.getPerson(id);
    }

    @PostMapping
    public Set<PersonObject> createPersons(@RequestBody Set<PersonObject> personObjects) {
        Set<PersonObject> createdPersons = new HashSet<>();
        for(PersonObject personObject: personObjects) {
            createdPersons.add(personService.createPerson(personObject));
        }
        return createdPersons;
    }

    @PostMapping(value = "id/{id}:recreateHash")
    public PersonObject recreateHash(@PathVariable Integer id) {
        return personService.recreateHash(id);
    }

    @DeleteMapping("/id/{id}")
    public void deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
    }
}
