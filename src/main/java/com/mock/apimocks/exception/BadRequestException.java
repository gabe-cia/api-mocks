package com.mock.apimocks.exception;

/**
 * This class represents an Bad Request Http response.
 * <p/>
 * It is meant to be thrown whenever the request body was structurally invalid.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class BadRequestException extends RuntimeException implements HttpError {
    private String description;

    public BadRequestException(String description) {
        super();
        this.description = description;
    }

    @Override
    public String getHttpError() {
        return "Bad Request";
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
