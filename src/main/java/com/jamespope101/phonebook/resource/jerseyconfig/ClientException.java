package com.jamespope101.phonebook.resource.jerseyconfig;

import javax.ws.rs.core.Response.StatusType;

/**
 * Created by jpope on 08/02/2018.
 */
public class ClientException extends HttpException {
    public ClientException(StatusType status) {
        super(status);
    }
}