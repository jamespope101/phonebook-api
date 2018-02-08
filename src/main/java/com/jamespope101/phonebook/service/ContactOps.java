package com.jamespope101.phonebook.service;

import java.util.List;

import com.jamespope101.phonebook.domain.Contact;

/**
 * Created by jpope on 08/02/2018.
 */
public interface ContactOps {

    List<Contact> getAllContacts();

    void createContact(Contact createSubmission);

    Contact findContact(Long id);

    void updateContact(Long id, Contact updateSubmission);

}
