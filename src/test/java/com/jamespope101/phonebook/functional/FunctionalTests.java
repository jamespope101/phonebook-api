package com.jamespope101.phonebook.functional;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.common.io.Resources;
import com.jayway.jsonassert.JsonAssert;
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
import org.junit.Test;

import static com.jamespope101.phonebook.repository.DatasetTemplates.ADDRESS_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_PHONE_NUMBER_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.CONTACT_TEMPLATE;
import static com.jamespope101.phonebook.repository.DatasetTemplates.PHONE_NUMBER_TEMPLATE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Created by jpope on 08/02/2018.
 */
public class FunctionalTests {

    private static final String READER_USER_AUTH_HEADER = "Basic " + Base64.encodeAsString("reader:reader");
    private static final String ADMIN_USER_AUTH_HEADER = "Basic " + Base64.encodeAsString("admin:admin");

    private String expectedContactsJson;
    private String contactCreateJson;
    private String contactCreateBadRequestJson;
    private String phoneNumberUpdateJson;

    @ClassRule
    public static TestApplication application = TestApplication.running();

    private DataSource dataSource;

    private Client client;

    private FlatXmlDataSet dataSet;

    @Before
    public void setup() throws IOException {
        this.client = ClientBuilder.newClient();
        this.dataSource = application.getApplication().context().getBean(DataSource.class);

        expectedContactsJson = Resources.toString(Resources.getResource("expected-contacts.json"), UTF_8);
        contactCreateJson = Resources.toString(Resources.getResource("contact-create.json"), UTF_8);
        contactCreateBadRequestJson = Resources.toString(Resources.getResource("contact-create-bad.json"), UTF_8);
        phoneNumberUpdateJson = Resources.toString(Resources.getResource("phone-number-update.json"), UTF_8);
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

    /**
     *  REST API Functional tests. From HTTP request to database and back again!
     */
    @Test
    public void shouldGetAllContactsSortedByLastName() {
        Response response = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        assertThat(response.readEntity(String.class)).isEqualTo(expectedContactsJson);
    }

    @Test
    public void shouldCreateANewContactIfAdminUser() {
        Response createResponse = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, ADMIN_USER_AUTH_HEADER)
            .post(Entity.entity(contactCreateJson, APPLICATION_JSON_TYPE));

        assertThat(createResponse.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        // check persistence worked by seeing if GET all contacts returns 1 more contact
        Response getResponse = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .get();

        assertThat(getResponse.getStatus()).isEqualTo(OK.getStatusCode());
        JsonAssert.with(getResponse.readEntity(String.class)).assertThat("$", hasSize(2 + 1));
    }

    @Test
    public void shouldDeleteAContact() {
        Response deleteResponse = client.target("http://localhost:2018/phonebook/contacts/2")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, ADMIN_USER_AUTH_HEADER)
            .delete();

        assertThat(deleteResponse.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        // check persistence worked by seeing if GET contact 2 no longer found
        Response getResponse = client.target("http://localhost:2018/phonebook/contacts/2")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .get();

        assertThat(getResponse.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
    }

    @Test
    public void shouldUpdateAPhoneNumber() {
        Response updateResponse = client.target("http://localhost:2018/phonebook/phone-numbers/1")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, ADMIN_USER_AUTH_HEADER)
            .put(Entity.entity(phoneNumberUpdateJson, APPLICATION_JSON_TYPE));

        assertThat(updateResponse.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        // check persistence worked by seeing if GET phone number 1 is the updated version
        Response getResponse = client.target("http://localhost:2018/phonebook/phone-numbers/1")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .get();

        assertThat(getResponse.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(getResponse.readEntity(String.class)).isEqualTo(phoneNumberUpdateJson);
    }

    /**
     * Exception Handler Test. The service layer can throw a BadRequestException. Do the exception handlers translate this into a 400 response?
     */
    @Test
    public void shouldGiveBadRequestResponse() {
        Response badCreateResponse = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, ADMIN_USER_AUTH_HEADER)
            .post(Entity.entity(contactCreateBadRequestJson, APPLICATION_JSON_TYPE));

        assertThat(badCreateResponse.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(badCreateResponse.readEntity(String.class)).isEqualTo("The provided address does not exist. Please create it first.");
    }

    /**
     * Authentication and authorisation tests. The admin and reader users were created in-memory in SecurityConfig.
     */
    @Test
    public void shouldNotAuthoriseIfNoAuthorisationHeaderProvided() {
        Response response = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());
        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.message", is("Full authentication is required to access this resource"));
    }

    @Test
    public void shouldNotAuthoriseIfIncorrectPassword() {
        Response response = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeAsString("hacker:password1?"))
            .get();

        assertThat(response.getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());
        JsonAssert.with(response.readEntity(String.class))
            .assertThat("$.message", is("Bad credentials"));
    }

    @Test
    public void shouldNotAuthoriseIfReaderUserPosts() {
        Response createResponse = client.target("http://localhost:2018/phonebook/contacts")
            .request(APPLICATION_JSON_TYPE)
            .header(HttpHeaders.AUTHORIZATION, READER_USER_AUTH_HEADER)
            .post(Entity.entity(contactCreateJson, APPLICATION_JSON_TYPE));

        assertThat(createResponse.getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

}
