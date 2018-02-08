package com.jamespope101.phonebook.resource;

import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.resource.jerseyconfig.JerseyConfig;
import com.jamespope101.phonebook.service.AddressOps;
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
import static com.jamespope101.phonebook.TestFixtures.ADDRESS_2;
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
public class AddressResourceTest extends JerseyTest {

    private static final List<Address> ADDRESSES = ImmutableList.of(ADDRESS_1, ADDRESS_2);

    @Mock
    private AddressOps addressOps;

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected Application configure() {
        MockitoAnnotations.initMocks(this);

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new JerseyConfig().registerInstances(new AddressResource(addressOps));
    }

    @Override
    protected URI getBaseUri() {
        return URI.create("http://feed/");
    }

    @Test
    public void shouldGetAllAddresses() {
        when(addressOps.getAllAddresses()).thenReturn(ADDRESSES);

        final Response response = target("/addresses")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$", hasSize(2))

            .assertThat("$[0].id", is(1))
            .assertThat("$[0].houseNumber", is("92"))
            .assertThat("$[0].streetName", is("Evergreen Terrace"))
            .assertThat("$[0].postcode", is("CO2 3"))
            .assertThat("$[0].country", is("UK"))

            .assertThat("$[1].id", is(2))
            .assertThat("$[1].houseNumber", is("Remote Moat"))
            .assertThat("$[1].streetName", is("Country Lane"))
            .assertThat("$[1].postcode", is("E3 211"))
            .assertThat("$[1].country", is("Sweden"));
    }

    @Test
    public void shouldGetSingleUser() {
        when(addressOps.findAddress(1L)).thenReturn(ADDRESS_1);

        final Response response = target("/addresses/1")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$.id", is(1))
            .assertThat("$.houseNumber", is("92"))
            .assertThat("$.streetName", is("Evergreen Terrace"))
            .assertThat("$.postcode", is("CO2 3"))
            .assertThat("$.country", is("UK"))
        ;
    }

    @Test
    public void shouldCreateANewAddress() {
        final Address addressSubmission = Address.builder()
            .houseNumber("24")
            .streetName("City Road")
            .postcode("E6 6EE")
            .country("UK")
            .build();

        final Response response = target("/addresses")
            .request()
            .post(Entity.entity(addressSubmission, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressOps).createAddress(addressCaptor.capture());
        assertThat(addressCaptor.getValue()).isEqualToComparingFieldByField(addressSubmission);
    }

    @Test
    public void shouldSubmitAddressForUpdate() {
        final Response response = target("/addresses/1")
            .request()
            .put(Entity.entity(ADDRESS_1, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressOps).updateAddress(eq(1L), addressCaptor.capture());
        assertThat(addressCaptor.getValue()).isEqualToComparingFieldByField(ADDRESS_1);
    }
}
