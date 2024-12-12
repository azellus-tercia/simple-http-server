package com.azellustercia.dto.request.comment;

public class EditCommentRequest {
    private String commentId;
    private String songName;
    private String singer;
    private String comment;
    private String headCommentId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHeadCommentId() {
        return headCommentId;
    }

    public void setHeadCommentId(String headCommentId) {
        this.headCommentId = headCommentId;
    }
}
