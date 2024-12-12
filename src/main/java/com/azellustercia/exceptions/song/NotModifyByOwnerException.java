package com.azellustercia.exceptions.song;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class NotModifyByOwnerException extends CommonException {
    public NotModifyByOwnerException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
