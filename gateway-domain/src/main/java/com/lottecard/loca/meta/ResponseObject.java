package com.lottecard.loca.meta;

public record ResponseObject<T>(
        int code,
        String message,
        String detail,
        T data
) {
    public static <T> ResponseObject<T> errorWithMessage(int code, String message, String detail, T data) {
        return new ResponseObject<>(code, message, detail, data);
    }

    public static <T> ResponseObject<T> fromErrorType(ErrorType errorType, String detail, T data) {
        return new ResponseObject<>(errorType.getCode(), errorType.getMessage(), detail, data);
    }
}