package com.jamespope101.phonebook.resource.jerseyconfig;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import static com.jamespope101.phonebook.resource.jerseyconfig.ExceptionHandlers.EXCEPTION_HANDLERS;

/**
 * Created by jpope on 08/02/2018.
 */
@Named
public class JerseyConfig extends ResourceConfig {

    private static final Class<?>[] PROVIDERS = {
        ObjectMapperResolver.class,
        LoggingFeature.class,
        ExceptionHandlers.class,
    };

    @Inject
    public JerseyConfig(@JaxRsResource Set<Object> instances) {
        registerInstances(instances);

        registerClasses(PROVIDERS);
        registerClasses(EXCEPTION_HANDLERS);
    }

    @VisibleForTesting // used to inject resource(s) under test in a JerseyTest
    public JerseyConfig(Object... resources) {
        this(Sets.newHashSet(resources));
    }

    public static Client newClient() {
        return ClientBuilder.newClient().register(HttpResponseStatusFilter.class);
    }

}
