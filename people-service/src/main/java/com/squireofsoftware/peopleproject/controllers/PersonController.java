package com.squireofsoftware.peopleproject.controllers;

import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.services.PersonService;
import com.squireofsoftware.peopleproject.services.QrCodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/people")
@CrossOrigin
public class PersonController {
    private final PersonService personService;
    private final QrCodeService qrCodeService;

    public PersonController(PersonService personService,
                            QrCodeService qrCodeService) {
        this.personService = personService;
        this.qrCodeService = qrCodeService;
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

    @GetMapping(value = "/id/{id}/qrcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getQrCode(@PathVariable Integer id) throws IOException {
        return qrCodeService.getQrCode(id);
    }

    @PostMapping(value = "/id/{id}/qrcode:recreate", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] recreateQrCode(@PathVariable Integer id) throws IOException {
        return qrCodeService.recreateQrCode(id);
    }

    @DeleteMapping("/id/{id}")
    public void deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
    }
}
