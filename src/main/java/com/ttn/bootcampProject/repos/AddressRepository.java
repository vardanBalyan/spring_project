package com.ttn.bootcampProject.repos;

import com.ttn.bootcampProject.entities.Address;
import com.ttn.bootcampProject.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address,Long> {

    @Query(value = "select * from address where user_id=:id", nativeQuery = true)
    public List<Address> findAddressByUserId(@Param("id") long id);

    Address findById(long id);

    @Query(value = "select id from address where user_id=:id",nativeQuery = true)
    List<Long> findAddressIdsForUserId(@Param("id") long id);

    @Query("from Address where user.id=:userId AND label=:label")
    Address findAddressByUserIdAndLabel(@Param("userId") long userId, @Param("label") String label);
}
