package com.azellustercia.handler.user;

import com.azellustercia.dto.request.user.LoginRequest;
import com.azellustercia.dto.response.user.LoginResponse;
import com.azellustercia.exceptions.user.AlreadyAuthorizedException;
import com.azellustercia.exceptions.user.RegisterException;
import com.azellustercia.exceptions.user.WrongPasswordException;
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

public class AuthorizationHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            LoginResponse response = new LoginResponse();
            LoginRequest loginRequest = Json.readValue(new String(request.getMessageBody()), LoginRequest.class);
            List<Field> emptyFields = checkDeclaredFields(loginRequest);
            if (emptyFields.isEmpty()) {
                UserService userService = new UserService();
                String token = userService.authorization(loginRequest);
                response.setLogin(loginRequest.getLogin());
                response.setToken(token);
                response.setMessage("Success");
                return HttpResponse.buildResponseMessage(
                        request.getBestCompatibleHttpVersion().LITERAL,
                        HttpStatusCode.OK,
                        Json.stringify(response)
                );
            } else {
                return errorEmptyFieldsBuilder(emptyFields, request);
            }
        } catch (JsonProcessingException e) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        } catch (RegisterException | WrongPasswordException | AlreadyAuthorizedException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
