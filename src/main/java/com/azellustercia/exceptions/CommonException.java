package com.azellustercia.exceptions;

import com.azellustercia.http.headers.HttpStatusCode;

public class CommonException extends Exception {
    private final HttpStatusCode errorCode;
    private final String errorMessage;

    public CommonException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
