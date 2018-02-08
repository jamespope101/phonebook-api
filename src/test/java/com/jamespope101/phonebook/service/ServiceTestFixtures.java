package com.jamespope101.phonebook.service;

import com.jamespope101.phonebook.domain.Address;
import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;

/**
 * Created by jpope on 08/02/2018.
 */
class ServiceTestFixtures {

    static final PhoneNumber PHONE_NUMBER_1 = PhoneNumber.builder()
        .id(1L)
        .type(PhoneType.mobile)
        .countryCode(44)
        .areaCode(772)
        .number(432813)
        .build();


    static final PhoneNumber PHONE_NUMBER_2 = PhoneNumber.builder()
        .id(2L)
        .type(PhoneType.home)
        .countryCode(44)
        .areaCode(151)
        .number(123456)
        .build();

    static final Address ADDRESS_1 = Address.builder()
        .id(1L)
        .houseNumber("92")
        .streetName("Evergreen Terrace")
        .postcode("CO2 3")
        .country("UK")
        .build();

    static final Address ADDRESS_2 = Address.builder()
        .id(2L)
        .houseNumber("Remote Moat")
        .streetName("Country Lane")
        .postcode("E3 211")
        .country("Sweden")
        .build();
}
