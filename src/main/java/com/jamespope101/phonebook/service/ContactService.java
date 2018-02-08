package com.jamespope101.phonebook.service;

import java.util.List;

import javax.ws.rs.NotFoundException;

import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jpope on 08/02/2018.
 */
@Service
public class ContactService implements ContactOps {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.getAllContacts();
    }

    @Override
    public void createContact(Contact createSubmission) {
        contactRepository.createContact(createSubmission);
    }

    @Override
    public Contact findContact(Long id) {
        return contactRepository.findContactById(id).orElseThrow(
            () -> new NotFoundException("Could not find address with id " + id));
    }

    @Override
    public void updateContact(Long id, Contact updateSubmission) {
        Contact existing = contactRepository.findContactById(id).orElseThrow(
            () -> new NotFoundException("Could not find address with id " + id));

        existing.setTitle(updateSubmission.getTitle());
        existing.setFirstName(updateSubmission.getFirstName());
        existing.setMiddleName(updateSubmission.getMiddleName());
        existing.setLastName(updateSubmission.getLastName());
        existing.setAddress(updateSubmission.getAddress());
        existing.setPhoneNumbers(updateSubmission.getPhoneNumbers());
    }
}
