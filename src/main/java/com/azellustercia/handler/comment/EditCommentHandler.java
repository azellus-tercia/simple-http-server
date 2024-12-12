package com.azellustercia.handler.comment;

import com.azellustercia.dto.request.comment.EditCommentRequest;
import com.azellustercia.dto.response.comment.EditCommentResponse;
import com.azellustercia.exceptions.comment.NoSuchCommentException;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.user.SessionNotExistsException;
import com.azellustercia.handler.Handler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.message.HttpRequest;
import com.azellustercia.http.message.HttpResponse;
import com.azellustercia.httpserver.util.Json;
import com.azellustercia.service.CommentService;
import com.azellustercia.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.Field;
import java.util.List;

public class EditCommentHandler extends Handler {
    @Override
    public HttpResponse handle(HttpRequest request) throws HttpParsingException {
        try {
            String token = checkAuthorizationHeader(request);
            String login = new UserService().getLoginByToken(token);
            EditCommentResponse response = new EditCommentResponse();
            EditCommentRequest editCommentRequest = Json.readValue(new String(request.getMessageBody()), EditCommentRequest.class);
            List<Field> emptyFields = checkDeclaredFields(editCommentRequest);
            if (emptyFields.isEmpty()) {
                CommentService commentService = new CommentService();
                commentService.editComment(editCommentRequest, login);
                response.setMessage("Comment successfully changed!");
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
        } catch (SessionNotExistsException | NoSuchSongException | NoSuchCommentException e) {
            return HttpResponse.buildErrorMessage(
                    request.getBestCompatibleHttpVersion().LITERAL,
                    e.getErrorCode(),
                    e.getErrorMessage()
            );
        }
    }
}
