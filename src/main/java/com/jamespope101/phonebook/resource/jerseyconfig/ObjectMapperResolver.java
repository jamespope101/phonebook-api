package com.jamespope101.phonebook.resource.jerseyconfig;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Created by jpope on 08/02/2018.
 */
@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return OBJECT_MAPPER;
    }
}