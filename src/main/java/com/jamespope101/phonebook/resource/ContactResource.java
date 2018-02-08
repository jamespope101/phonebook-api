package com.jamespope101.phonebook.resource;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.resource.jerseyconfig.JaxRsResource;
import com.jamespope101.phonebook.service.ContactOps;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by jpope on 08/02/2018.
 */
@JaxRsResource
@Path("/contacts")
public class ContactResource {

    private final ContactOps contactOps;

    @Inject
    ContactResource(ContactOps contactOps) {
        this.contactOps = contactOps;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Contact> getAllContacts() {
        return contactOps.getAllContacts();
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{contactId}")
    public Contact getContact(@PathParam("contactId") Long contactId) {
        return contactOps.findContact(contactId);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public void createContact(Contact contact) {
        contactOps.createContact(contact);
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/{contactId}")
    public void updateContact(@PathParam("contactId") Long contactId, Contact updated) {
        contactOps.updateContact(contactId, updated);
    }

    @DELETE
    @Path("/{contactId}")
    public void deleteContact(@PathParam("contactId") Long contactId) {
        contactOps.deleteContact(contactId);
    }
}
