package com.azellustercia.daoimpl;

import com.azellustercia.dao.SongDao;
import com.azellustercia.database.Database;
import com.azellustercia.dto.request.song.CreateSongRequest;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.exceptions.song.SongAlreadyExistsException;
import com.azellustercia.model.Song;
import com.azellustercia.model.data.GradeSong;

import java.util.*;

public class SongDaoImpl implements SongDao {
    @Override
    public void createSong(CreateSongRequest createSongRequest, String login) throws SongAlreadyExistsException {
        Database.getInstance().addSong(
                new Song(
                        createSongRequest.getName(),
                        createSongRequest.getComposers(),
                        createSongRequest.getAuthors(),
                        createSongRequest.getSinger(),
                        createSongRequest.getLength(),
                        login,
                        5,
                        new ArrayList<>(),
                        new HashMap<>(Map.of(login, 5))
                )
        );
    }

    @Override
    public boolean checkSongWithParameters(String songName, String singer) {
        return Database.getInstance().checkSongWithParameters(songName, singer);
    }

    @Override
    public void gradeSong(GradeSong gradeSong) throws NoSuchSongException, NotModifyByOwnerException {
        Database.getInstance().gradeSong(gradeSong);
    }

    @Override
    public List<Song> getConcert() {
        List<Song> result = Database.getInstance().getConcert();
        int[] summaryLength = {0};
        result.sort(Comparator.comparingDouble(Song::getAverageGrade).reversed().thenComparingInt(Song::getLength));
        return result.stream().takeWhile(song -> {
            if (summaryLength[0] + song.getLength() <= 3600) {
                summaryLength[0] += song.getLength();
                return true;
            }
            return false;
        }).toList();
    }
}
