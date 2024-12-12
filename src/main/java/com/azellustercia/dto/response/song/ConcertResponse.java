package com.azellustercia.dto.response.song;

import com.azellustercia.model.Song;

import java.util.List;

public class ConcertResponse {
    private List<Song> songs;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
