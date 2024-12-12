package com.azellustercia.handler.user;

import com.azellustercia.dto.response.user.LogOutResponse;
import com.azellustercia.exceptions.user.SessionNotExistsException;
import com.azellustercia.handler.Handler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;
import com.azellustercia.httpserver.util.Json;
import com.azellustercia.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class LogOutHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            String token = checkAuthorizationHeader(request);
            LogOutResponse response = new LogOutResponse();
            UserService userService = new UserService();
            userService.logOut(token);
            response.setToken(token);
            response.setMessage("Logout successfully");
            return HttpResponse.buildResponseMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    HttpStatusCode.OK,
                    Json.stringify(response)
            );
        } catch (JsonProcessingException e) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        } catch (SessionNotExistsException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
