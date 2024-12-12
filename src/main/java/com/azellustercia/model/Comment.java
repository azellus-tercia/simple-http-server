package com.azellustercia.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String login;
    private String songName;
    private String singer;
    private String comment;
    private String headComment;
    private String time;

    public Comment(String id, String login, String songName, String singer, String comment, String headComment, String time) {
        this.id = id;
        this.login = login;
        this.songName = songName;
        this.singer = singer;
        this.comment = comment;
        this.headComment = headComment;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getHeadComment() {
        return headComment;
    }

    public void setHeadComment(String headComment) {
        this.headComment = headComment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
