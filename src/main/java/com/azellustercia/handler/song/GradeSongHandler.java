package com.azellustercia.handler.song;

import com.azellustercia.dto.request.song.GradeSongRequest;
import com.azellustercia.dto.response.song.GradeSongResponse;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.exceptions.user.SessionNotExistsException;
import com.azellustercia.handler.Handler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;
import com.azellustercia.httpserver.util.Json;
import com.azellustercia.service.SongService;
import com.azellustercia.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Field;
import java.util.List;

public class GradeSongHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            String token = checkAuthorizationHeader(request);
            String login = new UserService().getLoginByToken(token);
            GradeSongResponse response = new GradeSongResponse();
            GradeSongRequest gradeSongRequest = Json.readValue(new String(request.getMessageBody()), GradeSongRequest.class);
            List<Field> emptyFields = checkDeclaredFields(gradeSongRequest);
            if (emptyFields.isEmpty()) {
                SongService songService = new SongService();
                songService.gradeSong(gradeSongRequest, login);
                response.setSongName(gradeSongRequest.getSongName().concat(" by ").concat(gradeSongRequest.getSinger()));
                response.setMessage("Grade successfully accepted!");
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
        } catch (SessionNotExistsException | NoSuchSongException | NotModifyByOwnerException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
