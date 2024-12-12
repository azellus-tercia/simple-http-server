package com.azellustercia.exceptions.comment;

import com.azellustercia.exceptions.CommonException;
import com.azellustercia.http.headers.HttpStatusCode;

public class NoSuchCommentException extends CommonException {
    public NoSuchCommentException(HttpStatusCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
