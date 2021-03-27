package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
   public User findByEmail(String email);

   @Query("from User where id=:id")
   public User findByUserId(@Param("id") long id);
}
