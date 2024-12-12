package com.azellustercia.service;

import com.azellustercia.dao.SongDao;
import com.azellustercia.daoimpl.SongDaoImpl;
import com.azellustercia.dto.request.song.CreateSongRequest;
import com.azellustercia.dto.request.song.GradeSongRequest;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.exceptions.song.SongAlreadyExistsException;
import com.azellustercia.exceptions.song.SongLengthIsNotValidException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.model.Song;
import com.azellustercia.model.data.GradeSong;

import java.util.List;

public class SongService {
    private final SongDao songDao = new SongDaoImpl();

    public void createSong(CreateSongRequest createSongRequest, String login) throws SongLengthIsNotValidException, SongAlreadyExistsException {
        if (createSongRequest.getLength() < 0 || createSongRequest.getLength() > 3600) {
            throw new SongLengthIsNotValidException(HttpStatusCode.CLIENT_ERROR_403_FORBIDDEN, "Song duration is not valid");
        }
        songDao.createSong(createSongRequest, login);
    }

    public void gradeSong(GradeSongRequest gradeSongRequest, String login) throws NoSuchSongException, NotModifyByOwnerException {
        GradeSong gradeSong = new GradeSong();
        gradeSong.setSongName(gradeSongRequest.getSongName());
        gradeSong.setSinger(gradeSongRequest.getSinger());
        gradeSong.setLogin(login);
        gradeSong.setGrade(gradeSongRequest.getGrade());
        songDao.gradeSong(gradeSong);
    }

    public List<Song> getConcert() {
        return songDao.getConcert();
    }
}
