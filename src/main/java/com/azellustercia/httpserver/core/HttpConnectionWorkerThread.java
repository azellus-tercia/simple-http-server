package com.azellustercia.httpserver.core;

import com.azellustercia.handler.comment.CreateCommentHandler;
import com.azellustercia.handler.comment.EditCommentHandler;
import com.azellustercia.handler.song.ConcertHandler;
import com.azellustercia.handler.song.CreateSongHandler;
import com.azellustercia.handler.song.GradeSongHandler;
import com.azellustercia.handler.user.AuthorizationHandler;
import com.azellustercia.handler.user.DeleteUserHandler;
import com.azellustercia.handler.user.LogOutHandler;
import com.azellustercia.handler.user.RegisterHandler;
import com.azellustercia.http.exceptions.HttpParsingException;
import com.azellustercia.http.headers.HttpHeaderName;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.http.headers.HttpVersion;
import com.azellustercia.http.message.*;
import com.azellustercia.http.headers.HttpTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private final Socket socket;
    private final HttpParser httpParser = new HttpParser();

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HttpRequest request = httpParser.parseHttpRequest(inputStream);
            HttpResponse response = handleRequest(request);

            outputStream.write(response.getResponseBytes());

            LOGGER.info(" * Connection Processing Finished.");
        } catch (IOException e) {
            LOGGER.error("Problem with communication", e);
        } catch (HttpParsingException e) {
            LOGGER.info("Bag Request", e);
            HttpResponse response = new HttpResponse.Builder()
                    .httpVersion(HttpVersion.HTTP_1_1.LITERAL)
                    .statusCode(e.getErrorCode())
                    .build();
            try {
                outputStream.write(response.getResponseBytes());
            } catch (IOException ex) {
                LOGGER.error("Problem with communication", e);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public HttpResponse handleRequest(HttpRequest request) throws HttpParsingException {
        return switch (request.getMethod()) {
            case GET -> {
                LOGGER.info(" * GET Request");
                yield handleGetRequest(request);
            }
            case HEAD -> {
                LOGGER.info(" * HEAD Request");
                yield handleGetRequest(request);
            }
            case POST -> {
                LOGGER.info(" * POST Request");
                yield handlePostRequest(request);
            }
            case PUT -> {
                LOGGER.info(" * PUT Request");
                yield handlePutRequest(request);
            }
            case DELETE -> {
                LOGGER.info(" * DELETE Request");
                yield handlePutRequest(request);
            }
        };
    }

    private HttpResponse handleGetRequest(HttpRequest request) throws HttpParsingException {
        switch (request.getRequestTarget()) {
            case HttpTarget.EMPTY -> {
                return new HttpResponse.Builder()
                        .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                        .statusCode(HttpStatusCode.OK)
                        .addHeader(HttpHeaderName.CONTENT_TYPE.headerName, "application/json")
                        .addHeader(HttpHeaderName.CONTENT_LENGTH.headerName, "0")
                        .build();
            }
            case HttpTarget.LOGOUT -> {
                return new LogOutHandler().handle(request);
            }
            case HttpTarget.CONCERT -> {
                return new ConcertHandler().handle(request);
            }
            case HttpTarget.DELETE -> {
                return new DeleteUserHandler().handle(request);
            }
            default -> {
                return new HttpResponse.Builder()
                        .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                        .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
                        .build();
            }
        }
    }

    private HttpResponse handlePostRequest(HttpRequest request) throws HttpParsingException {
        switch (request.getRequestTarget()) {
            case HttpTarget.REGISTER -> {
                return new RegisterHandler().handle(request);
            }
            case HttpTarget.AUTHORIZATION -> {
                return new AuthorizationHandler().handle(request);
            }
            case HttpTarget.CREATE_SONG -> {
                return new CreateSongHandler().handle(request);
            }
            case HttpTarget.CREATE_COMMENT -> {
                return new CreateCommentHandler().handle(request);
            }
            default -> {
                return new HttpResponse.Builder()
                        .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                        .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
                        .build();
            }
        }
    }

    private HttpResponse handlePutRequest(HttpRequest request) throws HttpParsingException {
        switch (request.getRequestTarget()) {
            case HttpTarget.GRADE_SONG -> {
                return new GradeSongHandler().handle(request);
            }
            case HttpTarget.EDIT_COMMENT -> {
                return new EditCommentHandler().handle(request);
            }
            default -> {
                return new HttpResponse.Builder()
                        .httpVersion(request.getBestCompatibleHttpVersion().LITERAL)
                        .statusCode(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED)
                        .build();
            }
        }
    }
}
