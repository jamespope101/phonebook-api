package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.PhoneNumber;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toSet;

/**
 * Created by jpope on 07/02/2018.
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class HibernateContactRepository implements ContactRepository {

    private final SessionFactory sessionFactory;
    private final AddressRepository addressRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Inject
    public HibernateContactRepository(SessionFactory sessionFactory, AddressRepository addressRepository,
                                      PhoneNumberRepository phoneNumberRepository) {
        this.sessionFactory = sessionFactory;
        this.addressRepository = addressRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }

    @Override
    public List<Contact> getContacts() {
        return (List<Contact>) sessionFactory.getCurrentSession()
            .createCriteria(Contact.class)
            .addOrder(Order.asc("lastName"))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .setReadOnly(true)
            .list();
    }

    @Override
    public Optional<Contact> findContactById(Long id) {
        return Optional.ofNullable((Contact) sessionFactory.getCurrentSession()
            .createCriteria(Contact.class)
            .add(Restrictions.idEq(id))
            .uniqueResult());
    }

    @Override
    public void createContact(Contact contact) {
        Optional<Address> daoAddress = addressRepository.findAddress(contact.getAddress().getId());
        contact.setAddress(daoAddress.orElseThrow(() -> new BadRequestException("The provided address does not exist. Please create it first.")));

        Set<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (!phoneNumbers.isEmpty()) {
            Set<Optional<PhoneNumber>> daoPhoneNumbers = phoneNumbers.stream()
                .map(phoneNumber -> phoneNumberRepository.findPhoneNumber(phoneNumber.getId())).collect(toSet());
            if (!daoPhoneNumbers.stream().allMatch(Optional::isPresent)) {
                throw new BadRequestException("At least one of the provided phone numbers does not exist. Please create it first.");
            } else {
                contact.setPhoneNumbers(daoPhoneNumbers.stream().map(Optional::get).collect(toSet()));
            }
        }
        sessionFactory.getCurrentSession().save(contact);
    }
}
