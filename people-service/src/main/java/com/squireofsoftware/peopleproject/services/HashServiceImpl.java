package com.squireofsoftware.peopleproject.services;

import com.squireofsoftware.peopleproject.dtos.NameObject;
import com.squireofsoftware.peopleproject.dtos.PersonObject;
import com.squireofsoftware.peopleproject.entities.PersonHash;
import com.squireofsoftware.peopleproject.jpas.JpaNamePart;
import com.squireofsoftware.peopleproject.jpas.JpaPersonHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class HashServiceImpl implements HashService {
    @Autowired
    private final JpaPersonHash jpaPersonHash;
    @Autowired
    private final JpaNamePart jpaNamePart;

    public HashServiceImpl(JpaPersonHash jpaPersonHash,
                           JpaNamePart jpaNamePart) {
        this.jpaPersonHash = jpaPersonHash;
        this.jpaNamePart = jpaNamePart;
    }

    @Override
    public Integer getHash(Integer personId) {
        return jpaPersonHash.findByPersonId(personId)
                .map(PersonHash::getHash)
                .orElse(null);
    }

    @Override
    public PersonObject getPerson(Integer hash) {
        return jpaPersonHash.findByHash(hash)
                .map(PersonHash::getPerson)
                .map(person ->
                        PersonObject.builder()
                                .id(person.getId())
                                .familyName(person.getFamilyName())
                                .givenName(person.getGivenName())
                                .isBaptised(person.getIsBaptised())
                                .isMember(person.getIsMember())
                                .hash(hash)
                                .otherNames(jpaNamePart.findByPersonId(person.getId())
                                        .stream().map(name -> NameObject.builder()
                                                .language(name.getType().name())
                                                .value(name.getValue())
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                                )
                                .build()
                )
                .orElse(null);
    }
}
