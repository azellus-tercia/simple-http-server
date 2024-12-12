package com.azellustercia.handler.song;

import com.azellustercia.dto.request.song.CreateSongRequest;
import com.azellustercia.dto.response.song.CreateSongResponse;
import com.azellustercia.exceptions.song.SongAlreadyExistsException;
import com.azellustercia.exceptions.song.SongLengthIsNotValidException;
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

public class CreateSongHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            String token = checkAuthorizationHeader(request);
            String login = new UserService().getLoginByToken(token);
            CreateSongResponse response = new CreateSongResponse();
            CreateSongRequest createSongRequest = Json.readValue(new String(request.getMessageBody()), CreateSongRequest.class);
            List<Field> emptyFields = checkDeclaredFields(createSongRequest);
            if (emptyFields.isEmpty()) {
                SongService songService = new SongService();
                songService.createSong(createSongRequest, login);
                response.setMessage("Song successfully added");
                response.setSong(createSongRequest.getName().concat(" by ").concat(createSongRequest.getSinger()));
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
        } catch (SessionNotExistsException | SongLengthIsNotValidException | SongAlreadyExistsException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
