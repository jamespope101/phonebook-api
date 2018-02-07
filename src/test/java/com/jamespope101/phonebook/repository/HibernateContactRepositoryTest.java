package com.jamespope101.phonebook.repository;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.Title;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static com.jamespope101.phonebook.domain.Title.MRS;
import static com.jamespope101.phonebook.repository.DatasetTemplates.ADDRESS_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_PHONE_NUMBER_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.PHONE_NUMBER_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by jpope on 07/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestContext.class)
@Transactional
public class HibernateContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    private static final TransactionDefinition TX_DEFINITION = new DefaultTransactionDefinition();

    private IDatabaseConnection connection;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    public void setup(DataSource dataSource) throws Exception {
        connection = new DatabaseDataSourceConnection(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setupDb() throws SQLException, DatabaseUnitException {
        MockitoAnnotations.initMocks(this);

        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

        final FlatXmlDataSet dataSet = RepositoryTestContext.createDataSet(
            String.format(PHONE_NUMBER_TEMPLATE, 1, "home", 44, 151, 234621),
            String.format(PHONE_NUMBER_TEMPLATE, 2, "mobile", 44, 774, 192031),
            String.format(ADDRESS_TEMPLATE, 1, "25", "Mean Street", "Z3 EQ2", "UK"),
            String.format(ADDRESS_TEMPLATE, 2, "Remote Moat", "Country Lane", "CO2 3", "Sweden"),
            String.format(CONTACT_TEMPLATE, 1, "MISS", "Lisa", "J", "Simpson", 2L),
            String.format(CONTACT_TEMPLATE, 2, "MR", "James", "Edward", "Pope", 1L),
            String.format(CONTACT_PHONE_NUMBER_TEMPLATE, 1L, 1L),
            String.format(CONTACT_PHONE_NUMBER_TEMPLATE, 1L, 2L) // contact 1 has phone numbers 1 and 2, contact 2 has no phone numbers
        );

        final Statement st = connection.getConnection().createStatement();
        st.execute("set REFERENTIAL_INTEGRITY false");
        st.close();

        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    @Test
    public void shouldGetAllContactsSortedAlphabeticallyByLastName() {
        List<Contact> contacts = contactRepository.getContacts();

        assertThat(contacts).hasSize(2)
            .extracting(Contact::getId, Contact::getTitle, Contact::getFirstName, Contact::getMiddleName, Contact::getLastName,
                contact -> contact.getAddress().getPostcode() // not a special identifier, but proof the full join entity was retrieved
            )
            .containsExactly( // note: sorted by surname
                tuple(2L, Title.MR, "James", "Edward", "Pope", "Z3 EQ2"),
                tuple(1L, Title.MISS, "Lisa", "J", "Simpson", "CO2 3")
            );

        assertThat(contacts.get(0).getPhoneNumbers()).isEmpty();
        assertThat(contacts.get(1).getPhoneNumbers()).extracting("number").containsOnly(234621, 192031);
    }

    @Test
    public void shouldFindContactById() {
        Contact contact = contactRepository.findContactById(2L).orElseThrow(AssertionError::new);
        assertThat(contact.getFirstName()).isEqualTo("James");
        assertThat(contact.getAddress().getPostcode()).isEqualTo("Z3 EQ2");
        assertThat(contact.getPhoneNumbers()).isEmpty();
    }

    @Test
    public void shouldReturnEmptyIfContactNotFound() {
        Optional<Contact> contact = contactRepository.findContactById(404L); // not in DB
        assertThat(contact).isEmpty();
    }

    @Test
    public void shouldCreateContactWithExistingAddressAndPhoneNumbers() throws SQLException, DataSetException {
        Address addressDao = sessionFactory.getCurrentSession().byId(Address.class).getReference(2L);
        PhoneNumber phoneNumberDao = sessionFactory.getCurrentSession().byId(PhoneNumber.class).getReference(1L);

        Contact newContact = Contact.builder().title(MRS).firstName("Marge").lastName("Simpson")
            .address(addressDao).phoneNumber(phoneNumberDao).build();

        contactRepository.createContact(newContact);
        flush();

        ITable contactTable = connection.createDataSet().getTable("contact");

        assertThat(contactTable.getRowCount()).isEqualTo(2 + 1);
        assertThat(contactTable.getValue(2, "id")).isEqualTo(BigInteger.valueOf(3L));
        assertThat(contactTable.getValue(2, "title")).isEqualTo("MRS");
        assertThat(contactTable.getValue(2, "first_name")).isEqualTo("Marge");
        assertThat(contactTable.getValue(2, "middle_name")).isNull();
        assertThat(contactTable.getValue(2, "last_name")).isEqualTo("Simpson");
        assertThat(contactTable.getValue(2, "address")).isEqualTo(BigInteger.valueOf(2L));

        ITable contactPhoneNumberRelationshipTable = connection.createDataSet().getTable("contact_phone_number");

        assertThat(contactPhoneNumberRelationshipTable.getRowCount()).isEqualTo(2 + 1);
        assertThat(contactPhoneNumberRelationshipTable.getValue(2, "contact")).isEqualTo(BigInteger.valueOf(3L));
        assertThat(contactPhoneNumberRelationshipTable.getValue(2, "phone_number")).isEqualTo(BigInteger.valueOf(1L));
    }

    @Test
    public void shouldThrowBadRequestExceptionIfContactCreatedWithUnknownAddress() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The provided address does not exist. Please create it first.");

        Address unknownAddress = Address.builder().id(404L).build();

        Contact newContact = Contact.builder().title(MRS).firstName("Marge").lastName("Simpson").address(unknownAddress).build();

        contactRepository.createContact(newContact);
    }

    @Test
    public void shouldThrowBadRequestExceptionIfContactCreatedWithUnknownPhoneNumber() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("At least one of the provided phone numbers does not exist. Please create it first.");

        Address addressDao = sessionFactory.getCurrentSession().byId(Address.class).getReference(2L);
        PhoneNumber unknownPhoneNumber = PhoneNumber.builder().id(404L).build();

        Contact newContact = Contact.builder().title(MRS).firstName("Marge").lastName("Simpson")
            .address(addressDao).phoneNumber(unknownPhoneNumber).build();

        contactRepository.createContact(newContact);
    }

    private void flush() {
        platformTransactionManager.getTransaction(TX_DEFINITION).flush();
    }
}
