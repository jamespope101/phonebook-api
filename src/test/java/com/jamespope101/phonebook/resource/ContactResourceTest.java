package com.jamespope101.phonebook.resource;

import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.Title;
import com.jamespope101.phonebook.resource.jerseyconfig.JerseyConfig;
import com.jamespope101.phonebook.service.ContactOps;
import com.jayway.jsonassert.JsonAssert;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.jamespope101.phonebook.TestFixtures.ADDRESS_1;
import static com.jamespope101.phonebook.TestFixtures.CONTACT_1;
import static com.jamespope101.phonebook.TestFixtures.CONTACT_2;
import static com.jamespope101.phonebook.TestFixtures.PHONE_NUMBER_2;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jpope on 08/02/2018.
 */
public class ContactResourceTest extends JerseyTest {

    private static final List<Contact> CONTACTS = ImmutableList.of(CONTACT_1, CONTACT_2);

    @Mock
    private ContactOps contactOps;

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected Application configure() {
        MockitoAnnotations.initMocks(this);

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new JerseyConfig().registerInstances(new ContactResource(contactOps));
    }

    @Override
    protected URI getBaseUri() {
        return URI.create("http://feed/");
    }

    @Test
    public void shouldGetAllContacts() {
        when(contactOps.getAllContacts()).thenReturn(CONTACTS);

        final Response response = target("/contacts")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$", hasSize(2))

            .assertThat("$[0].id", is(1))
            .assertThat("$[0].title", is("MR"))
            .assertThat("$[0].firstName", is("James"))
            .assertThat("$[0].middleName", is("Edward"))
            .assertThat("$[0].lastName", is("Pope"))
            .assertThat("$[0].address.id", is(1)) // assume rest of Address serialisation was OK
            .assertThat("$[0].phoneNumbers", hasSize(1))
            .assertThat("$[0].phoneNumbers[0].id", is(1)) // assume rest of PhoneNumbers serialisation was OK

            .assertThat("$[1].id", is(2))
            .assertThat("$[1].title", is("MX"))
            .assertThat("$[1].firstName", is("Alex"))
            .assertNull("$[1].middleName")
            .assertThat("$[1].lastName", is("Jones"))
            .assertThat("$[1].address.id", is(2))
            .assertThat("$[1].phoneNumbers", hasSize(1))
            .assertThat("$[1].phoneNumbers[0].id", is(2));
    }

    @Test
    public void shouldGetSingleContact() {
        when(contactOps.findContact(1L)).thenReturn(CONTACT_1);

        final Response response = target("/contacts/1")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$.id", is(1))
            .assertThat("$.title", is("MR"))
            .assertThat("$.firstName", is("James"))
            .assertThat("$.middleName", is("Edward"))
            .assertThat("$.lastName", is("Pope"))
            .assertThat("$.address.id", is(1)) // assume rest of Address serialisation was OK
            .assertThat("$.phoneNumbers", hasSize(1))
            .assertThat("$.phoneNumbers[0].id", is(1)) // assume rest of PhoneNumbers serialisation was OK
        ;
    }

    @Test
    public void shouldCreateANewContact() {
        final Contact contactSubmission = Contact.builder()
            .title(Title.DR)
            .firstName("Bob")
            .middleName("B")
            .lastName("Bobberson")
            .address(ADDRESS_1)
            .phoneNumber(PHONE_NUMBER_2)
            .build();

        final Response response = target("/contacts")
            .request()
            .post(Entity.entity(contactSubmission, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactOps).createContact(contactCaptor.capture());
        Contact capturedContact = contactCaptor.getValue();
        assertThat(capturedContact).isEqualToIgnoringGivenFields(contactSubmission, "address", "phoneNumbers");
        assertThat(capturedContact.getAddress()).isEqualToComparingFieldByField(ADDRESS_1);
        assertThat(capturedContact.getPhoneNumbers()).hasSize(1);
    }

    @Test
    public void shouldSubmitContactForUpdate() {
        final Response response = target("/contacts/1")
            .request()
            .put(Entity.entity(CONTACT_1, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactOps).updateContact(eq(1L), contactCaptor.capture());
        Contact capturedContact = contactCaptor.getValue();
        assertThat(capturedContact).isEqualToIgnoringGivenFields(CONTACT_1, "address", "phoneNumbers");
        assertThat(capturedContact.getAddress()).isEqualToComparingFieldByField(ADDRESS_1);
        assertThat(capturedContact.getPhoneNumbers()).hasSize(1);
    }

    @Test
    public void shouldDeleteContact() {
        final Response response = target("/contacts/1")
            .request()
            .delete();

        verify(contactOps).deleteContact(1L);
        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());
    }
}
