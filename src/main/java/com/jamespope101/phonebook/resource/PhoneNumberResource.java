package com.jamespope101.phonebook.resource;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.resource.jerseyconfig.JaxRsResource;
import com.jamespope101.phonebook.service.PhoneNumberOps;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by jpope on 08/02/2018.
 */
@JaxRsResource
@Path("/phone-numbers")
public class PhoneNumberResource {

    private final PhoneNumberOps phoneNumberOps;

    @Inject
    PhoneNumberResource(final PhoneNumberOps phoneNumberOps) {
        this.phoneNumberOps = phoneNumberOps;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<PhoneNumber> getAllPhoneNumberes() {
        return phoneNumberOps.getAllPhoneNumbers();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{phoneNumberId}")
    public PhoneNumber getPhoneNumber(@PathParam("phoneNumberId") Long phoneNumberId) {
        return phoneNumberOps.findPhoneNumber(phoneNumberId);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public void createPhoneNumber(PhoneNumber PhoneNumber) {
        phoneNumberOps.createPhoneNumber(PhoneNumber);
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{phoneNumberId}")
    public void updatePhoneNumber(@PathParam("phoneNumberId") Long phoneNumberId, PhoneNumber updated) {
        phoneNumberOps.updatePhoneNumber(phoneNumberId, updated);
    }

}
