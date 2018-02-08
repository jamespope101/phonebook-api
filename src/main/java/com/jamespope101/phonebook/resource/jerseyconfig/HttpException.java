package com.jamespope101.phonebook.resource.jerseyconfig;

import javax.ws.rs.core.Response.StatusType;

/**
 * Created by jpope on 08/02/2018.
 */
public class HttpException extends RuntimeException {

    protected HttpException(StatusType status) {
        super(String.format("Status [%s], Reason [%s].", status.getStatusCode(), status.getReasonPhrase()));
    }
}
