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

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.resource.jerseyconfig.JaxRsResource;
import com.jamespope101.phonebook.service.AddressOps;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by jpope on 08/02/2018.
 */
@JaxRsResource
@Path("/addresses")
public class AddressResource {

    private final AddressOps addressOps;

    @Inject
    AddressResource(final AddressOps addressOps) {
        this.addressOps = addressOps;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Address> getAllAddresses() {
        return addressOps.getAllAddresses();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{addressId}")
    public Address getAddress(@PathParam("addressId") Long addressId) {
        return addressOps.findAddress(addressId);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public void createAddress(Address address) {
        addressOps.createAddress(address);
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{addressId}")
    public void updateAddress(@PathParam("addressId") Long addressId, Address updated) {
        addressOps.updateAddress(addressId, updated);
    }

}
