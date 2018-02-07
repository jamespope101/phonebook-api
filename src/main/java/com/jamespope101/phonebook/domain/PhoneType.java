package com.jamespope101.phonebook.domain;

/**
 * Created by jpope on 07/02/2018.
 */
public enum PhoneType {
    home("home"),
    work("work"),
    mobile("mobile"),
    fax("fax");

    private final String value;

    PhoneType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
