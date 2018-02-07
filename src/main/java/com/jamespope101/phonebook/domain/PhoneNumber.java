package com.jamespope101.phonebook.domain;

import lombok.Builder;

/**
 * Created by jpope on 07/02/2018.
 */
@Builder
public class PhoneNumber {

    private PhoneType phoneType;
    private int countryCode;
    private int areaCode;
    private int phoneNumber;
}
