package com.rsjavasolutions.securityinmemory.infstracture.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@Slf4j
@Order(value = 9)
// This indicates order in which ControllerAdvice's handleException methods should be resolved by HandlerExceptionResolver.
// Methods from global handler should have higher order (therefore lower precedence) than more specific handlers, i.e. ContractExceptionHandler
@ControllerAdvice
public class GlobalExceptionHandler extends AbstractExceptionHandler {

    private final static String ERROR_MESSAGE = "Handling \"%s\" due to \"%s\"";

    @ExceptionHandler(RuntimeException.class)
    public final <T extends RuntimeException> ResponseEntity<ApiError> handleGenericException(final T ex, final WebRequest request) {
        final String exceptionName = ex.getClass().getSimpleName();
        final String internalUuid = UUID.randomUUID().toString().substring(10);

        final String message;
        if (ex.getMessage() == null) {
            message = "No exception message available";
        } else {
            message = ex.getMessage();
        }

        log.error(String.format(ERROR_MESSAGE, exceptionName, message));
        log.debug("Stacktrace: ", ex);

        final HttpStatus status = extractHttpStatus(ex).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        final HttpHeaders headers = new HttpHeaders();
        final ApiError errorBody = new ApiError(ex.getMessage(), exceptionName, internalUuid, ExceptionUtils.getStackTrace(ex));

        return handleExceptionInternal(ex, errorBody, headers, status, request);
    }
}