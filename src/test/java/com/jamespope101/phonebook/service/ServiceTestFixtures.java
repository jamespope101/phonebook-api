package com.jamespope101.phonebook.service;

import com.jamespope101.phonebook.domain.PhoneNumber;
import com.jamespope101.phonebook.domain.PhoneType;

/**
 * Created by jpope on 08/02/2018.
 */
public class ServiceTestFixtures {

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
}
