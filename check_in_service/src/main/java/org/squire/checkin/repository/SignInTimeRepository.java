package org.squire.checkin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.squire.checkin.entities.SignInDAO;

import java.sql.Timestamp;
import java.util.List;

public interface SignInTimeRepository extends CrudRepository<SignInDAO, Integer> {
    @Query("SELECT si FROM SignInDAO si " +
            "WHERE si.personId = :personId AND " +
            "si.signInTime > :beforeDate AND " +
            "si.signInTime < :afterDate")
    List<SignInDAO> findBySignInTimesBetween(@Param("personId") Integer personId, @Param("beforeDate") Timestamp beforeDate, @Param("afterDate") Timestamp afterDate);

    @Query("SELECT si FROM SignInDAO si " +
            "WHERE si.personId = :personId")
    Page<SignInDAO> findLatestSignInTime(@Param("personId") Integer personId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM SignInDAO si " +
            "WHERE si.personId = :personId")
    void deleteByPersonId(@Param("personId") Integer personId);
}
