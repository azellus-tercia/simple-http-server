package com.azellustercia.exceptions.song;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class SongAlreadyExistsException extends CommonException {
    public SongAlreadyExistsException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
