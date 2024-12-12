package com.azellustercia.handler.user;

import com.azellustercia.dto.request.user.DeleteUserRequest;
import com.azellustercia.dto.response.user.DeleteUserResponse;
import com.azellustercia.exceptions.user.NotAllowedToProceedException;
import com.azellustercia.exceptions.user.SessionNotExistsException;
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

public class DeleteUserHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            String token = checkAuthorizationHeader(request);
            String login = new UserService().getLoginByToken(token);
            DeleteUserResponse response = new DeleteUserResponse();
            DeleteUserRequest deleteUserRequest = Json.readValue(new String(request.getMessageBody()), DeleteUserRequest.class);
            List<Field> emptyFields = checkDeclaredFields(deleteUserRequest);
            if (emptyFields.isEmpty()) {
                UserService userService = new UserService();
                userService.deleteUser(deleteUserRequest, login);
                response.setMessage("User successfully deleted");
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
        } catch (SessionNotExistsException | NotAllowedToProceedException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
