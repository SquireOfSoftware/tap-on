package org.squire.checkin.repository;

import org.springframework.data.repository.CrudRepository;
import org.squire.checkin.entities.SignInDAO;

public interface SignInTimeRepository extends CrudRepository<SignInDAO, Integer> {
}
