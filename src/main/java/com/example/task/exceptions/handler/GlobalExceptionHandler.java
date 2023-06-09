package com.example.task.exceptions.handler;

import com.example.task.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionTypeNotFoundException.class)
    ResponseEntity handleBindErrors(TransactionTypeNotFoundException exception, HttpServletRequest request, HttpServletResponse response) {
        String message= "Transaction type not found";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorModel errorModel = new ErrorModel(LocalDate.now(), status,
                status.toString(), message, request.getRequestURI());

        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    ResponseEntity handleBindErrors(DateTimeParseException exception, HttpServletRequest request, HttpServletResponse response) {
        String message= "Parsing failed. Invalid LocalDateTime string";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorModel errorModel = new ErrorModel(LocalDate.now(), status,
                status.toString(), message, request.getRequestURI());
        log.info(message);

        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InCorrectCurrencyException.class)
    ResponseEntity handleBindErrors(InCorrectCurrencyException exception, HttpServletRequest request, HttpServletResponse response) {

         String message= "Currency code not found";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorModel errorModel = new ErrorModel(LocalDate.now(), status,
                status.toString(), message, request.getRequestURI());

        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    ResponseEntity handleBindErrors(NumberFormatException exception, HttpServletRequest request, HttpServletResponse response){

        String message= "Problem occurred while while parsing amount";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorModel errorModel = new ErrorModel(LocalDate.now(), status,
                status.toString(), message, request.getRequestURI());
        log.info(message);

        return new ResponseEntity<>(errorModel, status);
    }
}
