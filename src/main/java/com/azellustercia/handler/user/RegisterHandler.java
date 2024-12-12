package com.azellustercia.handler.user;

import com.azellustercia.dto.request.user.RegisterUserRequest;
import com.azellustercia.dto.response.user.RegisterUserResponse;
import com.azellustercia.exceptions.user.RegisterException;
import com.azellustercia.handler.Handler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;
import com.azellustercia.httpserver.util.Json;
import com.azellustercia.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Field;
import java.util.List;

public class RegisterHandler extends Handler {
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            RegisterUserResponse response = new RegisterUserResponse();
            RegisterUserRequest registerUserRequest = Json.readValue(new String(request.getMessageBody()), RegisterUserRequest.class);
            List<Field> emptyFields = checkDeclaredFields(registerUserRequest);
            if (emptyFields.isEmpty()) {
                UserService userService = new UserService();
                userService.create(registerUserRequest);
                response.setLogin(registerUserRequest.getLogin());
                response.setMessage("User successfully created");
                return HttpResponse.buildResponseMessage(
                        request.getBestCompatibleHttpVersion().LITERAL,
                        HttpStatusCode.CREATED,
                        Json.stringify(response)
                );
            } else {
                return errorEmptyFieldsBuilder(emptyFields, request);
            }
        } catch (JsonProcessingException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (RegisterException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
                    );
        }
    }
}
