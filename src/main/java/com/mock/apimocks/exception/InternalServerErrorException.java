package com.mock.apimocks.exception;

/**
 * This class represents an Internal Server Error Http response.
 * <p/>
 * It is meant to be thrown whenever a generic error occurs on the server.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class InternalServerErrorException extends RuntimeException implements HttpError {
    private final String description;

    public InternalServerErrorException(String description) {
        super();
        this.description = description;
    }

    @Override
    public String getHttpError() {
        return "Internal Server Error";
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
