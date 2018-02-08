package com.jamespope101.phonebook.resource.jerseyconfig;

import java.nio.file.AccessDeniedException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jpope on 08/02/2018.
 */
public final class ExceptionHandlers {

    private ExceptionHandlers() { }

    public static final Class<?>[] EXCEPTION_HANDLERS = {
        ClientErrorExceptionHandler.class,
        ServerErrorExceptionHandler.class,
        AccessDeniedExceptionHandler.class,
        RuntimeErrorExceptionHandler.class,
    };

    @Provider
    public static class ClientErrorExceptionHandler implements ExceptionMapper<ClientErrorException> {

        @Override
        public Response toResponse(ClientErrorException exception) {
            Response javaxResponse = exception.getResponse();

            return Response.status(javaxResponse.getStatus()).entity(exception.getMessage()).build();
        }
    }

    @Provider
    public static class ServerErrorExceptionHandler implements ExceptionMapper<ServerErrorException> {

        @Override
        public Response toResponse(ServerErrorException exception) {
            Response javaxResponse = exception.getResponse();

            return Response.status(javaxResponse.getStatus()).entity(exception.getMessage()).build();
        }
    }

    @Provider
    public static class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

        @Override
        public Response toResponse(AccessDeniedException exception) {
            exception.getMessage();
            return Response.status(Status.FORBIDDEN).entity("You don't have permission to access this resource.").build();
        }
    }

    @Provider
    public static class RuntimeErrorExceptionHandler implements ExceptionMapper<RuntimeException> {

        private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeErrorExceptionHandler.class);

        @Override
        public Response toResponse(RuntimeException exception) {
            LOGGER.error(exception.getMessage(), exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
        }
    }

}
