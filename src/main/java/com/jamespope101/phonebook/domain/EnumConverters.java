package com.jamespope101.phonebook.domain;

import java.util.Arrays;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by jpope on 07/02/2018.
 */
public class EnumConverters {

    public static <T extends Enum<T>> T fromValue(Class<T> clazz, String value) {
        return Arrays.stream(clazz.getEnumConstants())
            .filter(enumConstant -> enumConstant.name().equalsIgnoreCase(value))
            .findFirst().orElseThrow(() -> new IllegalArgumentException(value));
    }

    private EnumConverters() { }

    public abstract static class AbstractEnumConverter<E extends Enum<E>> implements AttributeConverter<E, String> {

        private Class<E> type;

        AbstractEnumConverter(Class<E> type) {
            this.type = type;
        }

        @Override
        public String convertToDatabaseColumn(E attribute) {
            return attribute.name();
        }

        @Override
        public E convertToEntityAttribute(String dbData) {
            return EnumConverters.fromValue(type, dbData);
        }
    }

    @Converter(autoApply = true)
    public static final class PhoneTypeConverter extends AbstractEnumConverter<PhoneType> {

        public PhoneTypeConverter() {
            super(PhoneType.class);
        }
    }
}
