package com.jamespope101.phonebook.functional;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.glassfish.jersey.internal.util.Base64;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;

import static com.jamespope101.phonebook.repository.DatasetTemplates.ADDRESS_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_PHONE_NUMBER_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.PHONE_NUMBER_TEMPLATE;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jpope on 08/02/2018.
 */
public class EndToEndTest {

    private static final String READER_USER_AUTH_HEADER = "Basic " + Base64.encodeAsString("reader:reader");

    @ClassRule
    public static TestApplication application = TestApplication.running();

    private PlatformTransactionManager platformTransactionManager;
    private DataSource dataSource;

    private Client client;

    private FlatXmlDataSet dataSet;

    @Before
    public void setup() {
        this.client = ClientBuilder.newClient();
        this.dataSource = application.getApplication().context().getBean(DataSource.class);
        this.platformTransactionManager = application.getApplication().context().getBean(PlatformTransactionManager.class);
    }

    @Before
    public void initialiseTestData() throws DatabaseUnitException, SQLException {
        final StringBuilder datasetBuilder = new StringBuilder("<dataset>");
        datasetBuilder.append(String.format(PHONE_NUMBER_TEMPLATE, 1, "home", 44, 151, 234621));
        datasetBuilder.append(String.format(PHONE_NUMBER_TEMPLATE, 2, "mobile", 44, 774, 192031));
        datasetBuilder.append(String.format(ADDRESS_TEMPLATE, 1, "25", "Mean Street", "Z3 EQ2", "UK"));
        datasetBuilder.append(String.format(ADDRESS_TEMPLATE, 2, "Remote Moat", "Country Lane", "CO2 3", "Sweden"));
        datasetBuilder.append(String.format(CONTACT_TEMPLATE, 1, "MISS", "Lisa", "J", "Simpson", 2L));
        datasetBuilder.append(String.format(CONTACT_TEMPLATE, 2, "MR", "James", "Edward", "Pope", 1L));
        datasetBuilder.append(String.format(CONTACT_PHONE_NUMBER_TEMPLATE, 1L, 1L));
        datasetBuilder.append(String.format(CONTACT_PHONE_NUMBER_TEMPLATE, 1L, 2L));

        dataSet = new FlatXmlDataSetBuilder()
            .setColumnSensing(true)
            .build(new StringReader(datasetBuilder.append("</dataset>").toString()));

        executeInDbUnit(DatabaseOperation.TRUNCATE_TABLE, dataSet);
        executeInDbUnit(DatabaseOperation.CLEAN_INSERT, dataSet);
    }

    private void executeInDbUnit(DatabaseOperation databaseOperation, IDataSet dataSet) throws SQLException, DatabaseUnitException {
        IDatabaseConnection conn = new DatabaseDataSourceConnection(dataSource);
        try {

            conn.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());

            try (Statement st = conn.getConnection().createStatement()) {
                st.execute("set REFERENTIAL_INTEGRITY false");
            }

            databaseOperation.execute(conn, dataSet);
        } finally {
            conn.close();
        }
    }

    @Ignore("fix test")
    @Test
    public void shouldGetAllContacts() {
        Response response = client.target("http://localhost:2018/phonebook/contacts")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        String json = response.readEntity(String.class);

        assertThat(json).isNotEmpty();
    }
}
