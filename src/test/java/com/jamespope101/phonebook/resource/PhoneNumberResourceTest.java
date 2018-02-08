package com.jamespope101.phonebook.resource;

import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;
import com.jamespope101.phonebook.resource.jerseyconfig.JerseyConfig;
import com.jamespope101.phonebook.service.PhoneNumberOps;
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

import static com.jamespope101.phonebook.TestFixtures.PHONE_NUMBER_1;
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
public class PhoneNumberResourceTest extends JerseyTest {

    private static final List<PhoneNumber> PHONE_NUMBERS = ImmutableList.of(PHONE_NUMBER_1, PHONE_NUMBER_2);

    @Mock
    private PhoneNumberOps phoneNumberOps;

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new InMemoryTestContainerFactory();
    }

    @Override
    protected Application configure() {
        MockitoAnnotations.initMocks(this);

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new JerseyConfig().registerInstances(new PhoneNumberResource(phoneNumberOps));
    }

    @Override
    protected URI getBaseUri() {
        return URI.create("http://feed/");
    }

    @Test
    public void shouldGetAllPhoneNumbers() {
        when(phoneNumberOps.getAllPhoneNumbers()).thenReturn(PHONE_NUMBERS);

        final Response response = target("/phone-numbers")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$", hasSize(2))

            .assertThat("$[0].id", is(1))
            .assertThat("$[0].type", is("mobile"))
            .assertThat("$[0].countryCode", is(44))
            .assertThat("$[0].areaCode", is(772))
            .assertThat("$[0].number", is(432813))

            .assertThat("$[1].id", is(2))
            .assertThat("$[1].type", is("home"))
            .assertThat("$[1].countryCode", is(44))
            .assertThat("$[1].areaCode", is(151))
            .assertThat("$[1].number", is(123456));
    }

    @Test
    public void shouldGetSinglePhoneNumber() {
        when(phoneNumberOps.findPhoneNumber(1L)).thenReturn(PHONE_NUMBER_1);

        final Response response = target("/phone-numbers/1")
            .request(APPLICATION_JSON_TYPE)
            .get();

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(APPLICATION_JSON_TYPE);

        String json = response.readEntity(String.class);
        JsonAssert.with(json)
            .assertThat("$.id", is(1))
            .assertThat("$.type", is("mobile"))
            .assertThat("$.countryCode", is(44))
            .assertThat("$.areaCode", is(772))
            .assertThat("$.number", is(432813));
    }

    @Test
    public void shouldCreateANewPhoneNumber() {
        final PhoneNumber phoneNumberSubmission = PhoneNumber.builder()
            .type(PhoneType.work)
            .countryCode(31)
            .areaCode(123)
            .number(392810)
            .build();

        final Response response = target("/phone-numbers")
            .request()
            .post(Entity.entity(phoneNumberSubmission, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<PhoneNumber> phoneNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
        verify(phoneNumberOps).createPhoneNumber(phoneNumberCaptor.capture());
        assertThat(phoneNumberCaptor.getValue()).isEqualToComparingFieldByField(phoneNumberSubmission);
    }

    @Test
    public void shouldSubmitPhoneNumberForUpdate() {
        final Response response = target("/phone-numbers/1")
            .request()
            .put(Entity.entity(PHONE_NUMBER_1, APPLICATION_JSON_TYPE));

        assertThat(response.getStatus()).isEqualTo(NO_CONTENT.getStatusCode());

        ArgumentCaptor<PhoneNumber> phoneNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
        verify(phoneNumberOps).updatePhoneNumber(eq(1L), phoneNumberCaptor.capture());
        assertThat(phoneNumberCaptor.getValue()).isEqualToComparingFieldByField(PHONE_NUMBER_1);
    }

}
