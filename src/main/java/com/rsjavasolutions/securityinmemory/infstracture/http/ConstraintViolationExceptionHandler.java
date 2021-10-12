package com.rsjavasolutions.securityinmemory.infstracture.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Order(value = 8)
@ControllerAdvice
public class ConstraintViolationExceptionHandler extends AbstractExceptionHandler {

    private static final String ERROR_MESSAGE = "Handling error due to database constraint: %s, exception: \"%s\"";

    private static final Map<String, String> constraintErrorMessageMap = new HashMap<>() {{
        put("land_register_unique_kw_number_constraint", "Numer księgi wieczystej musi być unikalny.");
        put("user_uid_number_constraint", "Numer uid użytkownika musi być unikalny");
        put("user_email_constraint", "Adres email użytkownika musi być unikalny");
        put("uk_l7q2fb9fv4pqw1m40p4nfi7ir", "Numer polisy musi być unikalny");
    }};

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ApiError> handleConstraintViolationException(final ConstraintViolationException ex, final WebRequest request) {
        String constraintName = ex.getConstraintName();
        String userMessage = Optional
                .ofNullable(constraintErrorMessageMap.get(constraintName))
                .orElse("Wystąpił błąd spowodowany regułami bazy danych.");

        log.error(String.format(ERROR_MESSAGE, constraintName, ex.getMessage()));

        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        final HttpHeaders headers = new HttpHeaders();
        final ApiError errorBody = new ApiError(userMessage, ConstraintViolationException.class.getSimpleName(), null, ExceptionUtils.getStackTrace(ex));

        return handleExceptionInternal(ex, errorBody, headers, status, request);
    }
}