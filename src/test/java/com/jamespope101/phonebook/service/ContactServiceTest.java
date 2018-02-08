package com.jamespope101.phonebook.service;

import java.util.Optional;
import javax.ws.rs.NotFoundException;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.Title;
import com.jamespope101.phonebook.repository.ContactRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.jamespope101.phonebook.service.ServiceTestFixtures.ADDRESS_1;
import static com.jamespope101.phonebook.service.ServiceTestFixtures.ADDRESS_2;
import static com.jamespope101.phonebook.service.ServiceTestFixtures.CONTACT_1;
import static com.jamespope101.phonebook.service.ServiceTestFixtures.CONTACT_2;
import static com.jamespope101.phonebook.service.ServiceTestFixtures.PHONE_NUMBER_1;
import static com.jamespope101.phonebook.service.ServiceTestFixtures.PHONE_NUMBER_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jpope on 08/02/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest {

    private Contact existingContact;

    @Mock
    private ContactRepository mockRepository;

    @InjectMocks
    private ContactService contactService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void declareObjects() { // tear down and set up object between tests to prevent state mutation
        existingContact = Contact.builder()
            .id(1L)
            .title(Title.MR)
            .firstName("James")
            .middleName("Edward")
            .lastName("Pope")
            .address(ADDRESS_1)
            .phoneNumber(PHONE_NUMBER_1)
            .build();
    }

    @Test
    public void shouldReturnAllPhoneNumbers() {
        when(mockRepository.getAllContacts()).thenReturn(ImmutableList.of(CONTACT_1, CONTACT_2));

        assertThat(contactService.getAllContacts()).containsExactly(CONTACT_1, CONTACT_2);
    }

    @Test
    public void shouldReturnFoundPhoneNumber() {
        when(mockRepository.findContactById(1L)).thenReturn(Optional.of(CONTACT_1));
        assertThat(contactService.findContact(1L)).isEqualTo(CONTACT_1);
    }

    @Test
    public void shouldThrowNotFoundExceptionForNonExistentPhoneNumber() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Could not find address with id 404");

        when(mockRepository.findContactById(404L)).thenReturn(Optional.empty());
        contactService.findContact(404L);
    }

    @Test
    public void shouldCreatePhoneNumber() {
        contactService.createContact(CONTACT_2);
        verify(mockRepository).createContact(CONTACT_2);
    }

    @Test
    public void shouldEditPhoneNumber() {
        when(mockRepository.findContactById(1L)).thenReturn(Optional.of(existingContact));
        Contact updateSubmission = Contact.builder()
            .id(1L)
            .title(Title.MRS)
            .firstName("Marge")
            .middleName(null)
            .lastName("Simpson")
            .address(ADDRESS_2)
            .phoneNumber(PHONE_NUMBER_2)
            .build();

        contactService.updateContact(1L, updateSubmission);

        assertThat(existingContact).isEqualToComparingFieldByField(updateSubmission);
    }

}
