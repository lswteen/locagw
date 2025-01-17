package com.lottecard.loca.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "INTERNAL_SERVER_ERROR"),
    API_NOT_FOUND(HttpStatus.NOT_FOUND, 1002, "NOT_FOUND"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 1003, "METHOD_NOT_ALLOWED"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, 1004, "MISSING_REQUEST_PARAMETER"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 1005, "BAD_REQUEST")
    ;

    HttpStatus httpStatus;
    int code;
    String message;

    /**
     * errortype httpStatus code 는 중복되면안되지면 혹시나.
     * 중복되었어도 기존값으로 대체해서 오류 예외처리
     */
    private static final Map<HttpStatus, ErrorType> ERROR_TYPE_MAP =
            Stream.of(values()).collect(Collectors.toMap(
                    ErrorType::getHttpStatus,
                    e -> e,
                    (existing, replacement) -> existing
            ));

    public static ErrorType fromHttpStatus(HttpStatusCode status) {
        return ERROR_TYPE_MAP.getOrDefault(status, INTERNAL_SERVER_ERROR);
    }
}
