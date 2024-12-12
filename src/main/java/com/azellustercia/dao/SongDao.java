package com.azellustercia.dao;

import com.azellustercia.dto.request.song.CreateSongRequest;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.exceptions.song.SongAlreadyExistsException;
import com.azellustercia.model.Song;
import com.azellustercia.model.data.GradeSong;

import java.util.List;

public interface SongDao {
    void createSong(CreateSongRequest createSongRequest, String login) throws SongAlreadyExistsException;
    boolean checkSongWithParameters(String songName, String singer);
    void gradeSong(GradeSong gradeSong) throws NoSuchSongException, NotModifyByOwnerException;
    List<Song> getConcert();
}
