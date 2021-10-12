package com.rsjavasolutions.securityinmemory.infstracture.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Slf4j
@ControllerAdvice
public abstract class AbstractExceptionHandler {

    protected ResponseEntity<ApiError> handleExceptionInternal(final Exception ex,
                                                               final ApiError body,
                                                               final HttpHeaders headers,
                                                               final HttpStatus status,
                                                               final WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }

    protected Optional<HttpStatus> extractHttpStatus(final Exception ex) {
        Class exClass = ex.getClass();
        while (!exClass.equals(Throwable.class)) {
            if (exClass.isAnnotationPresent(ResponseStatus.class)) {
                final ResponseStatus rs = (ResponseStatus) exClass.getAnnotation(ResponseStatus.class);
                return Optional.of(rs.value());
            } else {
                exClass = exClass.getSuperclass();
            }
        }
        return Optional.empty();
    }
}