package com.azellustercia.http.headers;

public enum HttpStatusCode {

    /* --- CLIENT ERRORS --- */
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_401_UNAUTHORIZED(401, "Unauthorized"),
    CLIENT_ERROR_403_FORBIDDEN(403, "Forbidden"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found"),
    CLIENT_ERROR_409_CONFLICT(409, "Conflict"),
    CLIENT_ERROR_422_UNPROCESSABLE_CONTENT(422, "Unprocessable Content"),

    /* --- SERVER ERRORS --- */
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "Http Version Not Supported"),

    /* --- SERVER SUCCESS --- */
    OK(200, "OK"),
    CREATED(201, "Created");


    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }

}
