package com.azellustercia.daoimpl;

import com.azellustercia.dao.CommentDao;
import com.azellustercia.database.Database;
import com.azellustercia.exceptions.comment.NoSuchCommentException;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.model.Comment;

public class CommentDaoImpl implements CommentDao {
    @Override
    public void createComment(Comment comment) throws NoSuchSongException, NotModifyByOwnerException {
        Database.getInstance().createComment(comment);
    }

    @Override
    public void editComment(Comment comment) throws NoSuchSongException, NoSuchCommentException {
        Database.getInstance().editComment(comment);
    }
}
