package com.jamespope101.phonebook.repository;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import com.jamespope101.phonebook.domain.Address;
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

import static com.jamespope101.phonebook.repository.DatasetTemplates.ADDRESS_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Created by jpope on 07/02/2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestContext.class)
@Transactional
public class HibernateAddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

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
            String.format(ADDRESS_TEMPLATE, 1, "25", "Mean Street", "Z3 EQ2", "UK"),
            String.format(ADDRESS_TEMPLATE, 2, "Remote Moat", "Country Lane", "CO2 3", "Sweden")
        );

        final Statement st = connection.getConnection().createStatement();
        st.execute("set REFERENTIAL_INTEGRITY false");
        st.close();

        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    @Test
    public void shouldGetAllAddresses() {
        List<Address> addresses = addressRepository.getAllAddresses();

        assertThat(addresses).hasSize(2).extracting("id", "houseNumber", "streetName", "postcode", "country")
            .containsExactly(
                tuple(1L, "25", "Mean Street", "Z3 EQ2", "UK"),
                tuple(2L, "Remote Moat", "Country Lane", "CO2 3", "Sweden")
            );
    }

    @Test
    public void shouldFindAddressById() {
        Optional<Address> address = addressRepository.findAddress(1L);
        Address expectedAddress = Address.builder().id(1L).houseNumber("25").streetName("Mean Street").postcode("Z3 EQ2").country("UK").build();
        assertThat(address.orElseThrow(AssertionError::new))
            .isEqualToComparingFieldByField(expectedAddress);
    }

    @Test
    public void shouldReturnEmptyIfAddressNotFound() {
        Optional<Address> address = addressRepository.findAddress(404L); // does not exist in DB
        assertThat(address).isEmpty();
    }

    @Test
    public void shouldCreateAddress() throws SQLException, DataSetException {
        Address newAddress = Address.builder().houseNumber("666").streetName("Park Avenue").postcode("NY123").country("USA").build();
        addressRepository.createAddress(newAddress);
        flush();

        ITable addressTable = connection.createDataSet().getTable("address");

        assertThat(addressTable.getRowCount()).isEqualTo(2 + 1);
        assertThat(addressTable.getValue(2, "id")).isEqualTo(BigInteger.valueOf(3L));
        assertThat(addressTable.getValue(2, "number")).isEqualTo("666");
        assertThat(addressTable.getValue(2, "street_name")).isEqualTo("Park Avenue");
        assertThat(addressTable.getValue(2, "postcode")).isEqualTo("NY123");
        assertThat(addressTable.getValue(2, "country")).isEqualTo("USA");
    }

    private void flush() {
        platformTransactionManager.getTransaction(TX_DEFINITION).flush();
    }
}
