package com.azellustercia.exceptions.user;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class RegisterException extends CommonException {
    public RegisterException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
