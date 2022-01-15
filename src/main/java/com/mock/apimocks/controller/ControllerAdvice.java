package com.mock.apimocks.controller;

import com.mock.apimocks.exception.*;
import com.mock.apimocks.models.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Rest Controller Advice class.
 * <p>
 * This class handles the exceptions that are not caught within the normal execution flow.
 * <p>
 * If an exception implements the {@link HttpError} interface, it will probably be handled
 * by one of this class' methods.
 * <p>
 * In case an exception does not implement the {@link HttpError} interface, it will be handled
 * as an Internal Server Error.
 *
 * @author gabriel.nascimento
 * @version 1.0
 */
@RestControllerAdvice
public class ControllerAdvice {
    /**
     * Handles the {@link ResourceNotFoundException} exception with a Not Found response.
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorMessage notFound(ResourceNotFoundException ex) {
        return new ErrorMessage(ex);
    }

    /**
     * Handles the {@link BadRequestException} exception with a Bad Request response.
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage badRequest(BadRequestException ex) {
        return new ErrorMessage(ex);
    }

    /**
     * Handles the {@link MethodArgumentNotValidException} exception with a Bad Request response.
     * <p>
     * This exception is thrown automatically by Spring whenever the request has a validation annotation
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage badRequestValidationFailed(MethodArgumentNotValidException ex) {
        Set<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage).collect(Collectors.toSet());

        return new ErrorMessage("Bad Request", "There are some problems with the request body", errors);
    }

    /**
     * Handles the {@link InternalServerErrorException} exception with am Internal Server Error response.
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage internalServerError(InternalServerErrorException ex) {
        return new ErrorMessage(ex);
    }

    /**
     * Handles the {@link UnprocessableEntityException} exception with an Unprocessable Entity response.
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(UnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorMessage unprocessableEntity(UnprocessableEntityException ex) {
        return new ErrorMessage(ex);
    }

    /**
     * Handles the whatever Exceptions left as an Internal Server Error.
     * <p>
     * This is our fallback method and handles all the exceptions that could not be handled on
     * the previous methods.
     *
     * @param ex The exception to be handled
     * @return a {@link ErrorMessage} object with the error response body
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage genericServerError(Exception ex) {
        return new ErrorMessage("Internal Server Error", ex.getMessage(), null);
    }
}
