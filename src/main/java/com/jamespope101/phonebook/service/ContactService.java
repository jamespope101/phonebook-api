package com.jamespope101.phonebook.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.repository.AddressRepository;
import com.jamespope101.phonebook.repository.ContactRepository;
import com.jamespope101.phonebook.repository.PhoneNumberRepository;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;

/**
 * Created by jpope on 08/02/2018.
 */
@Service
public class ContactService implements ContactOps {

    private ContactRepository contactRepository;
    private AddressRepository addressRepository;
    private PhoneNumberRepository phoneNumberRepository;

    @Inject
    ContactService(ContactRepository contactRepository, AddressRepository addressRepository, PhoneNumberRepository phoneNumberRepository) {
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }

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
            () -> new NotFoundException("Could not find contact with id " + id));
    }

    @Override
    public void updateContact(Long id, Contact updateSubmission) {
        Contact existing = contactRepository.findContactById(id).orElseThrow(
            () -> new NotFoundException("Could not find contact with id " + id));

        Long addressId = updateSubmission.getAddress().getId();
        Optional<Address> daoAddress = addressRepository.findAddress(addressId);
        if (!daoAddress.isPresent()) {
            throw new BadRequestException("Trying to give contact address with id " + addressId + ", which does not exist");
        } else {
            updateSubmission.setAddress(daoAddress.get());
        }

        Set<PhoneNumber> phoneNumbers = updateSubmission.getPhoneNumbers();
        if (!phoneNumbers.isEmpty()) {
            Set<Optional<PhoneNumber>> daoPhoneNumbers = phoneNumbers.stream()
                .map(phoneNumber -> phoneNumberRepository.findPhoneNumber(phoneNumber.getId())).collect(toSet());
            if (!daoPhoneNumbers.stream().allMatch(Optional::isPresent)) {
                throw new BadRequestException("Contact contains unknown phone number IDs");
            } else {
                updateSubmission.setPhoneNumbers(daoPhoneNumbers.stream().map(Optional::get).collect(toSet()));
            }
        }

        existing.setTitle(updateSubmission.getTitle());
        existing.setFirstName(updateSubmission.getFirstName());
        existing.setMiddleName(updateSubmission.getMiddleName());
        existing.setLastName(updateSubmission.getLastName());
        existing.setAddress(updateSubmission.getAddress());
        existing.setPhoneNumbers(updateSubmission.getPhoneNumbers());
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteContact(id);
    }
}
