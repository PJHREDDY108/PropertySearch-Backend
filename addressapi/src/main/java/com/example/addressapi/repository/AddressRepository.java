package com.example.addressapi.repository;

import com.example.addressapi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE " +
            "(:street = '' OR LOWER(a.street) LIKE LOWER(CONCAT('%', :street, '%'))) AND " +
            "(:city = '' OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:zip = '' OR a.zip LIKE CONCAT('%', :zip, '%'))")
    List<Address> search(@Param("street") String street,
                         @Param("city") String city,
                         @Param("zip") String zip);
}