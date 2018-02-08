package com.jamespope101.phonebook.resource.jerseyconfig;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.ImmutableMap;

/**
 * Created by jpope on 08/02/2018.
 */
@Provider
public class HttpResponseStatusFilter implements ClientResponseFilter {

    private static final Map<Family, Consumer<StatusType>> MAPPERS = ImmutableMap.of(
        Family.CLIENT_ERROR, status -> { throw new ClientException(status); },
        Family.SERVER_ERROR, status -> { throw new ServerException(status); }
    );

    private static final Consumer<StatusType> NULL_CONSUMER = ctx -> { };

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        Family family = responseContext.getStatusInfo().getFamily();
        MAPPERS.getOrDefault(family, NULL_CONSUMER).accept(responseContext.getStatusInfo());
    }
}
