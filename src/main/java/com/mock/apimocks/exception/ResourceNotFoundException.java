package com.mock.apimocks.exception;

/**
 * This class represents a Not Found Http response.
 * <p/>
 * It is meant to be thrown whenever the requested resource was not found
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException implements HttpError {
    private final String description;

    public ResourceNotFoundException(String description) {
        super();
        this.description = description;
    }

    @Override
    public String getHttpError() {
        return "Not Found";
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
