package com.jamespope101.phonebook.repository;

import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import com.jamespope101.phonebook.domain.PhoneNumber;
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
public class HibernatePhoneNumberRepository implements PhoneNumberRepository {

    private final SessionFactory sessionFactory;

    @Inject
    public HibernatePhoneNumberRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<PhoneNumber> getAllPhoneNumbers() {
        return (List<PhoneNumber>) sessionFactory.getCurrentSession()
            .createCriteria(PhoneNumber.class)
            .setReadOnly(true)
            .list();
    }

    @Override
    public Optional<PhoneNumber> findPhoneNumber(Long id) {
        return Optional.ofNullable((PhoneNumber) sessionFactory.getCurrentSession()
            .createCriteria(PhoneNumber.class)
            .add(Restrictions.idEq(id))
            .uniqueResult());
    }

    @Override
    public void createPhoneNumber(PhoneNumber phoneNumber) {
        sessionFactory.getCurrentSession().save(phoneNumber);
    }
}
