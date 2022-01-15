package com.mock.apimocks.exception;

/**
 * This class represents an Unprocessable Entity Http response.
 * <p/>
 * It is meant to be thrown whenever a request data was invalid by the business rules.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public class UnprocessableEntityException extends RuntimeException implements HttpError {
    private final String description;

    public UnprocessableEntityException(String description) {
        super();
        this.description = description;
    }

    @Override
    public String getHttpError() {
        return "Unprocessable Entity";
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
