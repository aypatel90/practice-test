package com.chatbot.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messagesource;

    protected ResponseEntity<Object> handelMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                        HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErrorResponse.Campo> errorList = new ArrayList<>();

        for(ObjectError objectError : exception.getBindingResult().getAllErrors()) {
            String name = ((FieldError)objectError).getField();
            String message = messagesource.getMessage(objectError, LocaleContextHolder.getLocale());
            errorList.add(new ErrorResponse.Campo(name, message));
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status.value() + "   --   "+status.is4xxClientError());
        errorResponse.setCampos(errorList);
        errorResponse.setTime(OffsetDateTime.now());
        errorResponse.setTitle("Bad Request !!!");
        return super.handleExceptionInternal(exception, errorResponse, headers, status, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = new ErrorResponse();
        response.setStatus(status.value() + " ==> " + status.getReasonPhrase());
        response.setTitle(ex.getMessage());
        response.setTime(OffsetDateTime.now());

        return handleExceptionInternal(ex, response, new HttpHeaders(), status, request);
    }

}
