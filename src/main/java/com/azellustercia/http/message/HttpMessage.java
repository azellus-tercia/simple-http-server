package com.azellustercia.http.message;

import java.util.HashMap;
import java.util.Set;

public abstract class HttpMessage {

    private final HashMap<String, String> headers = new HashMap<>();

    private byte[] messageBody = new byte[0];

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    void addHeader(String headerName, String headerField) {
        headers.put(headerName, headerField);
    }

    public byte[] getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(byte[] messageBody) {
        this.messageBody = messageBody;
    }
}
