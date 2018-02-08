package com.jamespope101.phonebook.service;

import java.util.Optional;
import javax.ws.rs.NotFoundException;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.repository.AddressRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.jamespope101.phonebook.TestFixtures.ADDRESS_1;
import static com.jamespope101.phonebook.TestFixtures.ADDRESS_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jpope on 08/02/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {

    private Address existingAddress;

    @Mock
    private AddressRepository mockRepository;

    @InjectMocks
    private AddressService addressService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void declareObjects() { // tear down and set up object between tests to prevent state mutation
        existingAddress = Address.builder()
            .id(1L)
            .houseNumber("92")
            .streetName("Evergreen Terrace")
            .postcode("CO2 3")
            .country("UK")
            .build();
    }

    @Test
    public void shouldReturnAllPhoneNumbers() {
        when(mockRepository.getAllAddresses()).thenReturn(ImmutableList.of(ADDRESS_1, ADDRESS_2));

        assertThat(addressService.getAllAddresses()).containsExactly(ADDRESS_1, ADDRESS_2);
    }

    @Test
    public void shouldReturnFoundPhoneNumber() {
        when(mockRepository.findAddress(1L)).thenReturn(Optional.of(ADDRESS_1));
        assertThat(addressService.findAddress(1L)).isEqualTo(ADDRESS_1);
    }

    @Test
    public void shouldThrowNotFoundExceptionForNonExistentPhoneNumber() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Could not find address with id 404");

        when(mockRepository.findAddress(404L)).thenReturn(Optional.empty());
        addressService.findAddress(404L);
    }

    @Test
    public void shouldCreatePhoneNumber() {
        addressService.createAddress(ADDRESS_2);
        verify(mockRepository).createAddress(ADDRESS_2);
    }

    @Test
    public void shouldEditPhoneNumber() {
        when(mockRepository.findAddress(1L)).thenReturn(Optional.of(existingAddress));
        Address updateSubmission = Address.builder()
            .id(1L)
            .houseNumber("666")
            .streetName("Park Avenue")
            .postcode("NY123")
            .country("USA")
            .build();

        addressService.updateAddress(1L, updateSubmission);

        assertThat(existingAddress).isEqualToComparingFieldByField(updateSubmission);
    }

}
