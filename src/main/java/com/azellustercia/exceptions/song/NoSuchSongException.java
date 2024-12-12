package com.azellustercia.exceptions.song;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class NoSuchSongException extends CommonException {
    public NoSuchSongException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
