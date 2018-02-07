package com.jamespope101.phonebook.domain;

import java.util.List;

import lombok.Builder;

/**
 * Created by jpope on 07/02/2018.
 */
@Builder
public class Contact {

    private Title title;
    private String firstName;
    private String middleName;
    private String lastName;
    private List<PhoneNumber> phoneNumbers;
    private String address;
}
