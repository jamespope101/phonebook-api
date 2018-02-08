package com.jamespope101.phonebook.service;

import java.util.List;

import com.jamespope101.phonebook.domain.PhoneNumber;

/**
 * Created by jpope on 08/02/2018.
 */
public interface PhoneNumberOps {

    List<PhoneNumber> getAllPhoneNumbers();

    void createPhoneNumber(PhoneNumber createSubmission);

    PhoneNumber findPhoneNumber(Long id);

    void updatePhoneNumber(Long id, PhoneNumber updateSubmission);

}
