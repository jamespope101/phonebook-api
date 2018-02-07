package com.jamespope101.phonebook.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by jpope on 07/02/2018.
 */
@Builder
@Getter
public class Address {

    private String houseNumber;
    private String streetName;
    private String postcode;
    private String country;
}
