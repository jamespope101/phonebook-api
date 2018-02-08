package com.jamespope101.phonebook;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.domain.Contact;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;
import com.jamespope101.phonebook.domain.Title;

/**
 * Created by jpope on 08/02/2018.
 */
public class TestFixtures {

    public static final PhoneNumber PHONE_NUMBER_1 = PhoneNumber.builder()
        .id(1L)
        .type(PhoneType.mobile)
        .countryCode(44)
        .areaCode(772)
        .number(432813)
        .build();

    public static final PhoneNumber PHONE_NUMBER_2 = PhoneNumber.builder()
        .id(2L)
        .type(PhoneType.home)
        .countryCode(44)
        .areaCode(151)
        .number(123456)
        .build();

    public static final Address ADDRESS_1 = Address.builder()
        .id(1L)
        .houseNumber("92")
        .streetName("Evergreen Terrace")
        .postcode("CO2 3")
        .country("UK")
        .build();

    public static final Address ADDRESS_2 = Address.builder()
        .id(2L)
        .houseNumber("Remote Moat")
        .streetName("Country Lane")
        .postcode("E3 211")
        .country("Sweden")
        .build();

    public static final Contact CONTACT_1 = Contact.builder()
        .id(1L)
        .title(Title.MR)
        .firstName("James")
        .middleName("Edward")
        .lastName("Pope")
        .address(ADDRESS_1)
        .phoneNumber(PHONE_NUMBER_1)
        .build();

    public static final Contact CONTACT_2 = Contact.builder()
        .id(2L)
        .title(Title.MX)
        .firstName("Alex")
        .middleName(null)
        .lastName("Jones")
        .address(ADDRESS_2)
        .phoneNumber(PHONE_NUMBER_2)
        .build();
}
