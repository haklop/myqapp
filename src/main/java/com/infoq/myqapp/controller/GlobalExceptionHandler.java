package com.infoq.myqapp.controller;

import com.infoq.myqapp.domain.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity handleClientException(HttpClientErrorException e) {
        logger.error("Unknown error while usinh an external service", e);
        return new ResponseEntity<>(new ErrorMessage(e.getStatusCode().value(), "unknownError", "Error while using an external web service"),
                e.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException e) {
        logger.debug("Validation error", e);
        return new ResponseEntity<>(new ErrorMessage(400, "invalidArgument", "Body not valid"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity handleServerException(HttpServerErrorException e) {
        logger.error("Unknown error while usinh an external service", e);
        return new ResponseEntity<>(new ErrorMessage(e.getStatusCode().value(), "unknownError", "Error while using an external web service"),
                e.getStatusCode());
    }
}
