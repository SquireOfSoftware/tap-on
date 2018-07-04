package org.squire.checkin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.squire.checkin.entities.AlternativeNameDAO;
import org.squire.checkin.entities.Language;

import java.util.List;

public interface AlternativeNameRepository extends CrudRepository<AlternativeNameDAO, Integer> {
    @Query("SELECT an from AlternativeNameDAO an " +
            "WHERE an.alternativeName = personId")
    List<AlternativeNameDAO> findByPerson(Integer personId);

    @Query("SELECT an from AlternativeNameDAO an " +
            "WHERE an.alternativeName = personId AND an.language = language")
    AlternativeNameDAO findByLanguage(Integer personId, Language language);
}
