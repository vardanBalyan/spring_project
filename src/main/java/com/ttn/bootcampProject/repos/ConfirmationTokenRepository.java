package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.ConfirmationToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);

    @Query(value = "select * from confirmation_token where user_id=:id",nativeQuery = true)
    ConfirmationToken findByUserId(@Param("id") long id);
}
