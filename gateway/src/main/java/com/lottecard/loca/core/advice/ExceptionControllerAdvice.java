package com.lottecard.loca.core.advice;


import com.lottecard.loca.FormatUtils;
import com.lottecard.loca.HttpUtils;
import com.lottecard.loca.core.exception.ResourceInitializeException;
import com.lottecard.loca.meta.ErrorType;
import com.lottecard.loca.meta.ResponseObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final String ERROR_PAGE = "static/error.html";
    private static final String ERROR_PAGE_STRING;

    static {
        Resource resource = new ClassPathResource(ERROR_PAGE);
        try {
            ERROR_PAGE_STRING = resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResourceInitializeException("Resource " + ERROR_PAGE + " could not be found or initialized.");
        }
    }

    @ExceptionHandler(value = {Throwable.class, Exception.class})
    public ResponseEntity<?> exceptionHandler(ServerWebExchange exchange, Exception e) {
        log.error("ExceptionControllerAdvice Unhandled exception. {} : {} : {}", e.getClass(), e.getStackTrace(), e.getMessage());

        return response(exchange
                , ErrorType.INTERNAL_SERVER_ERROR
                , Optional.ofNullable(e.getCause())
                        .map(Throwable::getMessage)
                        .orElse(e.getMessage())
        );
    }

    /**
     * webflux ResponseStatusException HTTP 상태코드 관련 오류처리
     * @param exchange
     * @param e
     * @return
     */
    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException e) {
        String logMessage = FormatUtils.formatLogMessage(
                String.format("%s ( %s ) StackTrace: %s",
                e.getStatusCode(),
                e.getClass().getName(),
                Arrays.toString(e.getStackTrace()))
        );
        log.error(logMessage);
        ErrorType errorType = ErrorType.fromHttpStatus(e.getStatusCode());
        return response(exchange, errorType, e.getMessage());
    }

    private ResponseEntity<?> response(ServerWebExchange exchange, ErrorType errorType, String message) {
        exchange.getResponse().setStatusCode(errorType.getHttpStatus());
        return HttpUtils.isApi(exchange.getRequest().getURI()) ?
                responseBody(exchange, errorType, message) : responsePage(exchange,errorType.getHttpStatus());
    }

    private ResponseEntity<ResponseObject<?>> responseBody(ServerWebExchange exchange, ErrorType errorType, String message) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ResponseObject<?> errorResponse = ResponseObject.fromErrorType(errorType, message, message);
        return new ResponseEntity<>(errorResponse, errorType.getHttpStatus());
    }

    private ResponseEntity<String> responsePage(ServerWebExchange exchange, HttpStatus httpStatus) {
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(ERROR_PAGE_STRING, httpStatus);
    }

}