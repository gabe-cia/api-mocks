package com.mock.apimocks.exception;

/**
 * This interface represents a Http Error
 * <p/>
 * Whenever a class implements it, it means that this class represents a Http Error.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
public interface HttpError {
    String getHttpError();
    String getDescription();
}
