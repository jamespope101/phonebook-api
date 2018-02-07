package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;

import com.jamespope101.phonebook.domain.Address;

/**
 * Created by jpope on 07/02/2018.
 */
public interface AddressRepository {

    List<Address> getAllAddresses();

    Optional<Address> findAddress(Long id);

    void createAddress(Address address);
}
