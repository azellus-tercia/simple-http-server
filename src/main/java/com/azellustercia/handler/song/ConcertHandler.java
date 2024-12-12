package com.azellustercia.handler.song;

import com.azellustercia.dto.response.song.ConcertResponse;
import com.azellustercia.exceptions.user.SessionNotExistsException;
import com.azellustercia.handler.Handler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;
import com.azellustercia.httpserver.util.Json;
import com.azellustercia.service.SongService;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ConcertHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            checkAuthorizationHeader(request);
            ConcertResponse response = new ConcertResponse();
            SongService songService = new SongService();
            response.setSongs(songService.getConcert());
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
