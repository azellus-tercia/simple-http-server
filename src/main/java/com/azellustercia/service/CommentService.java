package com.azellustercia.service;

import com.azellustercia.dao.CommentDao;
import com.azellustercia.daoimpl.CommentDaoImpl;
import com.azellustercia.dto.request.comment.CreateCommentRequest;
import com.azellustercia.dto.request.comment.EditCommentRequest;
import com.azellustercia.exceptions.comment.NoSuchCommentException;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.model.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CommentService {
    private final CommentDao commentDao = new CommentDaoImpl();

    public String createComment(CreateCommentRequest request, String login) throws NoSuchSongException, NotModifyByOwnerException {
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                login,
                request.getSongName(),
                request.getSinger(),
                request.getComment(),
                request.getHeadCommentId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.ss"))
        );
        commentDao.createComment(comment);
        return comment.getId();
    }

    public void editComment(EditCommentRequest request, String login) throws NoSuchSongException, NoSuchCommentException {
        Comment comment = new Comment(
                request.getCommentId(),
                login,
                request.getSongName(),
                request.getSinger(),
                request.getComment(),
                request.getHeadCommentId(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.ss"))
        );
        commentDao.editComment(comment);
    }
}
