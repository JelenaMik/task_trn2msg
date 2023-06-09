package com.example.task.exceptions.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class ErrorModel {

    private LocalDate timestamp;
    private HttpStatus status;
    private String errorMessage;
    private String message;
    private String path;

}