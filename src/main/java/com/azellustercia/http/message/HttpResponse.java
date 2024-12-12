package com.azellustercia.http.message;

import com.azellustercia.dto.response.ErrorMessage;
import com.azellustercia.http.headers.HttpHeaderName;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.httpserver.util.Json;

public class HttpResponse extends HttpMessage {

    private static final String CRLF = "\r\n";

    private String httpVersion;

    private HttpStatusCode statusCode;

    private String reasonPhrase = null;

    private HttpResponse() {
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        if (reasonPhrase == null && statusCode != null) {
            return statusCode.MESSAGE;
        }
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public byte[] getResponseBytes() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(httpVersion)
                .append(" ")
                .append(statusCode.STATUS_CODE)
                .append(" ")
                .append(getReasonPhrase())
                .append(CRLF);

        for (String headerName: getHeaderNames()) {
            responseBuilder.append(headerName)
                    .append(": ")
                    .append(getHeader(headerName))
                    .append(CRLF);
        }

        responseBuilder.append(CRLF);

        byte[] responseBytes = responseBuilder.toString().getBytes();

        if (getMessageBody().length == 0) {
            return responseBytes;
        }

        byte[] responseWithBody = new byte[responseBytes.length + getMessageBody().length];
        System.arraycopy(responseBytes, 0, responseWithBody, 0, responseBytes.length);
        System.arraycopy(getMessageBody(), 0, responseWithBody, responseBytes.length, getMessageBody().length);

        return responseWithBody;
    }

    public static class Builder {

        private final HttpResponse response = new HttpResponse();

        public Builder httpVersion (String httpVersion) {
            response.setHttpVersion(httpVersion);
            return this;
        }

        public Builder statusCode(HttpStatusCode statusCode) {
            response.setStatusCode(statusCode);
            return this;
        }

        public Builder reasonPhrase(String reasonPhrase) {
            response.setReasonPhrase(reasonPhrase);
            return this;
        }

        public Builder addHeader(String headerName, String headerField) {
            response.addHeader(headerName, headerField);
            return this;
        }

        public Builder messageBody(byte[] messageBody) {
            response.setMessageBody(messageBody);
            return this;
        }

        public HttpResponse build() {
            return response;
        }

    }

    public static HttpResponse buildErrorMessage(String httpVersion, HttpStatusCode httpStatusCode, String message) {
        return buildResponseMessage(httpVersion, httpStatusCode, Json.toJson(new ErrorMessage(message)).toPrettyString());
    }

    public static HttpResponse buildResponseMessage(String httpVersion, HttpStatusCode httpStatusCode, String message) {
        return new Builder()
                .httpVersion(httpVersion)
                .statusCode(httpStatusCode)
                .addHeader(HttpHeaderName.CONTENT_TYPE.headerName, "application/json")
                .addHeader(HttpHeaderName.CONTENT_LENGTH.headerName, String.valueOf(message.length()))
                .messageBody(message.getBytes())
                .build();
    }
}
