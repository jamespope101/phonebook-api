package com.jamespope101.phonebook.service;

import java.util.Optional;
import javax.ws.rs.NotFoundException;

import com.google.common.collect.ImmutableList;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;
import com.jamespope101.phonebook.repository.PhoneNumberRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.jamespope101.phonebook.TestFixtures.PHONE_NUMBER_2;
import static com.jamespope101.phonebook.TestFixtures.PHONE_NUMBER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jpope on 08/02/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhoneNumberServiceTest {

    private PhoneNumber existingPhoneNumber;

    @Mock
    private PhoneNumberRepository mockRepository;

    @InjectMocks
    private PhoneNumberService phoneNumberService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void declareObjects() { // tear down and set up object between tests to prevent state mutation
        existingPhoneNumber = PhoneNumber.builder()
            .id(1L)
            .type(PhoneType.home)
            .countryCode(44)
            .areaCode(151)
            .number(123456)
            .build();
    }

    @Test
    public void shouldReturnAllPhoneNumbers() {
        when(mockRepository.getAllPhoneNumbers()).thenReturn(ImmutableList.of(PHONE_NUMBER_1, PHONE_NUMBER_2));

        assertThat(phoneNumberService.getAllPhoneNumbers()).containsExactly(PHONE_NUMBER_1, PHONE_NUMBER_2);
    }

    @Test
    public void shouldReturnFoundPhoneNumber() {
        when(mockRepository.findPhoneNumber(1L)).thenReturn(Optional.of(PHONE_NUMBER_1));
        assertThat(phoneNumberService.findPhoneNumber(1L)).isEqualTo(PHONE_NUMBER_1);
    }

    @Test
    public void shouldThrowNotFoundExceptionForNonExistentPhoneNumber() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Could not find phone number with id 404");

        when(mockRepository.findPhoneNumber(404L)).thenReturn(Optional.empty());
        phoneNumberService.findPhoneNumber(404L);
    }

    @Test
    public void shouldCreatePhoneNumber() {
        phoneNumberService.createPhoneNumber(PHONE_NUMBER_2);
        verify(mockRepository).createPhoneNumber(PHONE_NUMBER_2);
    }

    @Test
    public void shouldEditPhoneNumber() {
        when(mockRepository.findPhoneNumber(1L)).thenReturn(Optional.of(existingPhoneNumber));
        PhoneNumber updateSubmission = PhoneNumber.builder().id(1L).type(PhoneType.fax).countryCode(47).areaCode(888).number(654321).build();

        phoneNumberService.updatePhoneNumber(1L, updateSubmission);

        assertThat(existingPhoneNumber).isEqualToComparingFieldByField(updateSubmission);
    }
}
