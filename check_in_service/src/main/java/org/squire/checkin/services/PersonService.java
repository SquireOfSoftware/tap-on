package org.squire.checkin.services;

import org.squire.checkin.entities.Person;
import reactor.core.publisher.Flux;

public interface PersonService {
    Flux<Iterable<Person>> getPersons();
}
