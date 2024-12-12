package com.azellustercia.exceptions.user;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class AlreadyAuthorizedException extends CommonException {
    public AlreadyAuthorizedException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
