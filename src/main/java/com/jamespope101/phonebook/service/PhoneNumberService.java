package com.jamespope101.phonebook.service;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.repository.PhoneNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jpope on 08/02/2018.
 */
@Service
@Transactional
public class PhoneNumberService implements PhoneNumberOps {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Override
    public List<PhoneNumber> getAllPhoneNumbers() {
        return phoneNumberRepository.getAllPhoneNumbers();
    }

    @Override
    public void createPhoneNumber(PhoneNumber createSubmission) {
        phoneNumberRepository.createPhoneNumber(createSubmission);
    }

    @Override
    public PhoneNumber findPhoneNumber(Long id) {
        return phoneNumberRepository.findPhoneNumber(id).orElseThrow(
            () -> new NotFoundException("Could not find phone number with id " + id));
    }

    @Override
    public void updatePhoneNumber(Long id, PhoneNumber updateSubmission) {
        PhoneNumber existing = phoneNumberRepository.findPhoneNumber(id).orElseThrow(
                () -> new NotFoundException("Could not find phone number with id " + id));

        existing.setType(updateSubmission.getType());
        existing.setCountryCode(updateSubmission.getCountryCode());
        existing.setAreaCode(updateSubmission.getAreaCode());
        existing.setNumber(updateSubmission.getNumber());
    }
}
