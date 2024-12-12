package com.azellustercia.exceptions.song;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class SongLengthIsNotValidException extends CommonException {
    public SongLengthIsNotValidException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
