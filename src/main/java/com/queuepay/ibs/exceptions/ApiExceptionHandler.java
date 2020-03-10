package com.queuepay.ibs.exceptions;

import com.queuepay.ibs.errorResponse.RequestError;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new RequestError(BAD_REQUEST, error, ex));
//        return super.handleMissingServletRequestParameter(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        RequestError error = new RequestError(BAD_REQUEST);
        error.setMessage("Validation error");
        error.addValidationErrors(ex.getBindingResult().getFieldErrors());
        error.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> handleCustom(
            CustomException ex) {
        RequestError error = new RequestError(ex.getStatus());
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<Object> handleHttpClientError(
            HttpClientErrorException ex) {
        RequestError error = new RequestError(ex.getStatusCode());
        error.setMessage(ex.getResponseBodyAsString());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        RequestError error = new RequestError(NOT_FOUND);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> handleEmptyResultDataAccess(
            EmptyResultDataAccessException ex) {
        RequestError error = new RequestError(NOT_FOUND);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        RequestError error = new RequestError(UNPROCESSABLE_ENTITY);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(RequestError error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}
