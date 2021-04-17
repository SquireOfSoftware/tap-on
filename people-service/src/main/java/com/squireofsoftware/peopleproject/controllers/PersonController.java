package com.squireofsoftware.peopleproject.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.squireofsoftware.peopleproject.dtos.PersonCSV;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.dtos.PersonReferenceObject;
import com.squireofsoftware.peopleproject.services.PersonService;
import com.squireofsoftware.peopleproject.services.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
@CrossOrigin
@Slf4j
public class PersonController {
    private final PersonService personService;
    private final QrCodeService qrCodeService;
    static final String CSV_MEDIA_TYPE = "text/csv";

    public PersonController(PersonService personService,
                            QrCodeService qrCodeService) {
        this.personService = personService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(value = "/")
    public List<PersonReferenceObject> getPeople() {
        return personService.getAllPeople();
    }

    @GetMapping(value = "/from/{from}")
    public List<PersonReferenceObject> getPeople(@PathVariable
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime from) {
        return personService.getAllPeopleFrom(from);
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

    @PutMapping("/id/{id}")
    public PersonObject updatePerson(@PathVariable Integer id, @RequestBody PersonObject updatedPerson) {
        return personService.updatePerson(id, updatedPerson);
    }

    @DeleteMapping("/id/{id}")
    public void deletePerson(@PathVariable Integer id) {
        personService.deletePerson(id);
    }

    @PostMapping("/import")
    public Set<PersonObject> importFromCSV(@RequestParam("file") MultipartFile file) throws Exception {
        if (hasCSVFormat(file)) {
            HeaderColumnNameMappingStrategy<PersonCSV> ms = new HeaderColumnNameMappingStrategy<>();
            ms.setType(PersonCSV.class);

            Reader reader;
            reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .build();

            CsvToBean<PersonCSV> beanConverter = new CsvToBeanBuilder<PersonCSV>(csvReader)
                    .withMappingStrategy(ms)
                    .withType(PersonCSV.class)
                    .build();

            return createPersons(beanConverter.parse().stream()
                    .map(PersonObject::map)
                    .collect(Collectors.toSet()));

        } else {
            throw new InvalidMediaTypeException(CSV_MEDIA_TYPE, "This endpoint will only support CSVs");
        }
    }

    private boolean hasCSVFormat(MultipartFile file) {
        return CSV_MEDIA_TYPE.equals(file.getContentType());
    }
}
