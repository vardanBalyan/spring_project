package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends CrudRepository<Role, Integer> {

    @Query("select authority from Role where id=:roleId")
    public String findAuthorityById(@Param("roleId") long roleId);
}
