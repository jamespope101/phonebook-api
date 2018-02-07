package com.jamespope101.phonebook.repository;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;
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
import org.junit.Test;
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

import static com.jamespope101.phonebook.repository.DatasetTemplates.PHONE_NUMBER_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by jpope on 07/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestContext.class)
@Transactional
public class HibernatePhoneNumberRepositoryTest {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

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

    @Before
    public void setupDb() throws SQLException, DatabaseUnitException {
        MockitoAnnotations.initMocks(this);

        connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

        final FlatXmlDataSet dataSet = RepositoryTestContext.createDataSet(
            String.format(PHONE_NUMBER_TEMPLATE, 1, "home", 44, 151, 234621),
            String.format(PHONE_NUMBER_TEMPLATE, 2, "mobile", 44, 774, 192031)
        );

        final Statement st = connection.getConnection().createStatement();
        st.execute("set REFERENTIAL_INTEGRITY false");
        st.close();

        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    @Test
    public void shouldGetAllPhoneNumbers() {
        List<PhoneNumber> phoneNumbers = phoneNumberRepository.getAllPhoneNumbers();

        assertThat(phoneNumbers).hasSize(2).extracting("id", "type", "countryCode", "areaCode", "number")
            .containsExactly(
                tuple(1L, PhoneType.home, 44, 151, 234621),
                tuple(2L, PhoneType.mobile, 44, 774, 192031)
            );
    }

    @Test
    public void shouldFindPhoneNumberById() {
        Optional<PhoneNumber> phoneNumber = phoneNumberRepository.findPhoneNumber(1L);
        PhoneNumber expectedPhoneNumber = PhoneNumber.builder().id(1L).type(PhoneType.home).countryCode(44).areaCode(151).number(234621).build();
        assertThat(phoneNumber.orElseThrow(AssertionError::new))
            .isEqualToComparingFieldByField(expectedPhoneNumber);
    }

    @Test
    public void shouldReturnEmptyIfPhoneNumberNotFound() {
        Optional<PhoneNumber> phoneNumber = phoneNumberRepository.findPhoneNumber(404L); // does not exist in DB
        assertThat(phoneNumber).isEmpty();
    }

    @Test
    public void shouldCreatePhoneNumber() throws SQLException, DataSetException {
        PhoneNumber newPhoneNumber = PhoneNumber.builder().type(PhoneType.work).countryCode(44).areaCode(181).number(123456).build();
        phoneNumberRepository.createPhoneNumber(newPhoneNumber);
        flush();

        ITable phoneNumbers = connection.createDataSet().getTable("phone_number");

        assertThat(phoneNumbers.getRowCount()).isEqualTo(2 + 1);
        assertThat(phoneNumbers.getValue(2, "id")).isEqualTo(BigInteger.valueOf(3L));
        assertThat(phoneNumbers.getValue(2, "type")).isEqualTo("work");
        assertThat(phoneNumbers.getValue(2, "country_code")).isEqualTo(44);
        assertThat(phoneNumbers.getValue(2, "area_code")).isEqualTo(181);
        assertThat(phoneNumbers.getValue(2, "number")).isEqualTo(123456);
    }

    private void flush() {
        platformTransactionManager.getTransaction(TX_DEFINITION).flush();
    }
}
