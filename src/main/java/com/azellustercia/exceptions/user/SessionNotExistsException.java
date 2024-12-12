package com.azellustercia.exceptions.user;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class SessionNotExistsException extends CommonException {
    public SessionNotExistsException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
