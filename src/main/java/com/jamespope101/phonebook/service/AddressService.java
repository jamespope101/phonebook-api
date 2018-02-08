package com.jamespope101.phonebook.service;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jpope on 08/02/2018.
 */
@Service
public class AddressService implements AddressOps {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.getAllAddresses();
    }

    @Override
    public void createAddress(Address createSubmission) {
        addressRepository.createAddress(createSubmission);
    }

    @Override
    public Address findAddress(Long id) {
        return addressRepository.findAddress(id).orElseThrow(
            () -> new NotFoundException("Could not find address with id " + id));
    }

    @Override
    public void updateAddress(Long id, Address updateSubmission) {
        Address existing = addressRepository.findAddress(id).orElseThrow(
            () -> new NotFoundException("Could not find address with id " + id));

        existing.setHouseNumber(updateSubmission.getHouseNumber());
        existing.setStreetName(updateSubmission.getStreetName());
        existing.setPostcode(updateSubmission.getPostcode());
        existing.setCountry(updateSubmission.getCountry());
    }
}
