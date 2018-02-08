package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;

import com.jamespope101.phonebook.domain.Contact;

/**
 * Created by jpope on 07/02/2018.
 */
public interface ContactRepository {

    List<Contact> getAllContacts();

    Optional<Contact> findContactById(Long id);

    void createContact(Contact contact);

    void deleteContact(Long contactId);
}
