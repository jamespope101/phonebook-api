package com.jamespope101.phonebook.service;

import java.util.List;

import com.jamespope101.phonebook.domain.Address;

/**
 * Created by jpope on 08/02/2018.
 */
public interface AddressOps {

    List<Address> getAllAddresses();

    void createAddress(Address createSubmission);

    Address findAddress(Long id);

    void updateAddress(Long id, Address updateSubmission);

}
