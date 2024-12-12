package com.azellustercia.dto.response.song;

public class CreateSongResponse {
    private String message;
    private String song;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
