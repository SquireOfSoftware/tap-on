package org.squire.checkin.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.squire.checkin.entities.Person;
import org.squire.checkin.repository.PersonRepository;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

@Slf4j
@Service
public class PersonServiceImpl implements PersonService {
    private PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Flux<Iterable<Person>> getPersons() {
        return Flux.fromStream(Stream.of(personRepository.findAll()));
    }
}
