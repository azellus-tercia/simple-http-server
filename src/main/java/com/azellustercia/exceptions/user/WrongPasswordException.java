package com.azellustercia.exceptions.user;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class WrongPasswordException extends CommonException {
    public WrongPasswordException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
