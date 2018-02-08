package com.jamespope101.phonebook.repository;

/**
 * Created by jpope on 07/02/2018.
 */
public class DatasetTemplates {

    public static final String PHONE_NUMBER_TEMPLATE = "<phone_number id=\"%d\" type=\"%s\" country_code=\"%d\" area_code=\"%d\" number=\"%d\" />";

    public static final String ADDRESS_TEMPLATE = "<address id=\"%d\" number=\"%s\" street_name=\"%s\" postcode=\"%s\" country=\"%s\" />";

    public static final String CONTACT_TEMPLATE = "<contact id=\"%d\" title=\"%s\" first_name=\"%s\" middle_name=\"%s\" last_name=\"%s\" address=\"%d\" />";

    public static final String CONTACT_PHONE_NUMBER_TEMPLATE = "<contact_phone_number contact=\"%d\" phone_number=\"%d\" />";
}
