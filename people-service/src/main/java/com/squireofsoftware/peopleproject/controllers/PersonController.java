package com.squireofsoftware.peopleproject.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.squireofsoftware.peopleproject.dtos.PersonCSV;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.services.PersonService;
import com.squireofsoftware.peopleproject.services.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
@CrossOrigin
@Slf4j
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

    @PostMapping("/import")
    public Set<PersonObject> importFromCSV(@RequestParam("file") MultipartFile file) {
        if (hasCSVFormat(file)) {
            HeaderColumnNameMappingStrategy<PersonCSV> ms = new HeaderColumnNameMappingStrategy<>();
            ms.setType(PersonCSV.class);

            Reader reader;
            try {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return Collections.emptySet();
    }

    private boolean hasCSVFormat(MultipartFile file) {
        return "text/csv".equals(file.getContentType());
    }
}
