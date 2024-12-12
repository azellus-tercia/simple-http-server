package com.azellustercia.http.headers;

public enum HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    AUTHORIZATION("Authorization");

    public final String headerName;

    HttpHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
