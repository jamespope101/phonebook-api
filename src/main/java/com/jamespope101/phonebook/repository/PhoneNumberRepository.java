package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;

import com.jamespope101.phonebook.domain.PhoneNumber;

/**
 * Created by jpope on 07/02/2018.
 */
public interface PhoneNumberRepository {

    List<PhoneNumber> getAllPhoneNumbers();

    Optional<PhoneNumber> findPhoneNumber(Long id);

    void createPhoneNumber(PhoneNumber phoneNumber);
}
