package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import com.jamespope101.phonebook.domain.Address;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jpope on 07/02/2018.
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class HibernateAddressRepository implements AddressRepository {

    private final SessionFactory sessionFactory;

    @Inject
    public HibernateAddressRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Address> getAllAddresses() {
        return (List<Address>) sessionFactory.getCurrentSession()
            .createCriteria(Address.class)
            .list();
    }

    @Override
    public Optional<Address> findAddress(Long id) {
        return Optional.ofNullable((Address) sessionFactory.getCurrentSession()
            .createCriteria(Address.class)
            .add(Restrictions.idEq(id))
            .uniqueResult());
    }

    @Override
    public void createAddress(Address address) {
        sessionFactory.getCurrentSession().save(address);
    }
}
