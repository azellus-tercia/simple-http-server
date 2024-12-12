package com.azellustercia.handler;

import com.azellustercia.exceptions.user.SessionNotExistsException;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpHeaderName;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class Handler {
    public abstract HttpResponse handle(HttpRequest request) throws HttpParsingException;

    public List<Field> checkDeclaredFields(Object object) {
        List<Field> fields = Arrays.stream(object.getClass().getDeclaredFields()).toList();
        fields.forEach(field -> field.setAccessible(true));
        return fields.stream().filter(field -> {
            try {
                return field.get(object) == null || field.get(object).toString().isEmpty();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public HttpResponse errorEmptyFieldsBuilder(List<Field> emptyFields, HttpRequest request) {
        StringBuilder builder = new StringBuilder();
        emptyFields.forEach(field -> builder.append(" '".concat(field.getName()).concat("'")));
        builder.insert(0, "Fields");
        builder.append(" are empty");
        return HttpResponse.buildErrorMessage(
                request.getBestCompatibleHttpVersion().LITERAL,
                HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST,
                builder.toString()
        );
    }

    public String checkAuthorizationHeader(HttpRequest request) throws SessionNotExistsException {
        String token = request.getHeader(HttpHeaderName.AUTHORIZATION.headerName);
        if (token == null || token.isEmpty()) {
            throw new SessionNotExistsException(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED, "Missing authorization header");
        }
        return token;
    }
}
