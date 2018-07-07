package org.squire.checkin.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.squire.checkin.entities.AlternativeNameDAO;
import org.squire.checkin.entities.Language;

import java.util.List;

public interface AlternativeNameRepository extends CrudRepository<AlternativeNameDAO, Integer> {
    @Query("SELECT an from AlternativeNameDAO an " +
            "WHERE an.personId = :personId")
    List<AlternativeNameDAO> findByPerson(@Param("personId") Integer personId);

    @Query("SELECT an from AlternativeNameDAO an " +
            "WHERE an.personId = :personId AND an.language = :language")
    AlternativeNameDAO findByLanguage(@Param("personId") Integer personId, @Param("language") Language language);

    @Transactional
    @Modifying
    @Query("DELETE FROM AlternativeNameDAO an " +
            "WHERE an.personId = :personId")
    void deleteByPersonId(@Param("personId") Integer personId);
}
