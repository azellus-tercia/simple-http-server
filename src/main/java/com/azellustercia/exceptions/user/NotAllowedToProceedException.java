package com.azellustercia.exceptions.user;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class NotAllowedToProceedException extends CommonException {
    public NotAllowedToProceedException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
