package com.azellustercia.dao;

import com.azellustercia.exceptions.comment.NoSuchCommentException;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.model.Comment;

public interface CommentDao {
    void createComment(Comment comment) throws NoSuchSongException, NotModifyByOwnerException;
    void editComment(Comment comment) throws NoSuchSongException, NoSuchCommentException;
}
