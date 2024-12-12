package com.azellustercia.database;

import com.azellustercia.exceptions.comment.NoSuchCommentException;
import com.azellustercia.exceptions.song.NoSuchSongException;
import com.azellustercia.exceptions.song.NotModifyByOwnerException;
import com.azellustercia.exceptions.song.SongAlreadyExistsException;
import com.azellustercia.exceptions.user.RegisterException;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.model.Comment;
import com.azellustercia.model.Song;
import com.azellustercia.model.User;
import com.azellustercia.model.data.GradeSong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
    private final List<User> users = new ArrayList<>();
    private final List<Song> songs = new ArrayList<>();
    private final Map<String, String> activeTokens = new HashMap<>();

    private static Database instance;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private Database() {}

    public synchronized static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void addUser(User newUser) throws RegisterException {
        try {
            lock.writeLock().lock();
            if (users.stream().noneMatch(user -> user.getLogin().equals(newUser.getLogin()))) {
                users.add(newUser);
            } else {
                throw new RegisterException(HttpStatusCode.CLIENT_ERROR_409_CONFLICT, "User with same login exists!");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addUserSession(String login, String date) throws RegisterException {
        try {
            lock.writeLock().lock();
            users.stream().filter(user -> user.getLogin().equals(login))
                    .findFirst()
                    .orElseThrow(() -> new RegisterException(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND, "User not found!"))
                    .getSessionHistory().add(date);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean checkExists(String login) {
        try {
            lock.readLock().lock();
            return users.stream().anyMatch(user -> user.getLogin().equals(login));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean authorization(String login, String password) {
        try {
            lock.readLock().lock();
            return users.stream().anyMatch(user -> user.getLogin().equals(login) && user.getPassword().equals(password));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean checkActiveSession(String login) {
        try {
            lock.readLock().lock();
            return activeTokens.containsKey(login);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean checkActiveSessionToken(String token) {
        try {
            lock.readLock().lock();
            return activeTokens.containsValue(token);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getLoginByToken(String token) {
        try {
            lock.readLock().lock();
            return Database.getInstance().getTokens().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(token))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, String> getTokens() {
        try {
            lock.readLock().lock();
            return activeTokens;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addToken(String login, String token) {
        try {
            lock.writeLock().lock();
            activeTokens.merge(login, token, (key, value) -> value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeToken(String login) {
        try {
            lock.writeLock().lock();
            activeTokens.remove(login);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean checkSongWithParameters(String songName, String singer) {
        try {
            lock.readLock().lock();
            return songs.stream().anyMatch(song -> song.getName().equals(songName) && song.getSinger().equals(singer));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addSong(Song newSong) throws SongAlreadyExistsException {
        try {
            lock.writeLock().lock();
            if (songs.stream().anyMatch(song -> song.getName().equals(newSong.getName()) && song.getSinger().equals(newSong.getSinger()))) {
                throw new SongAlreadyExistsException(HttpStatusCode.CLIENT_ERROR_409_CONFLICT, "Song with such name and singer already exists!");
            }
            songs.add(newSong);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void deleteUserSong(String login) {
        songs.removeIf(song -> song.getUser().equals(login));
    }

    private Song findSongByName(String songName, String singer) throws NoSuchSongException {
        return songs.stream().filter(song -> song.getName().equals(songName) && song.getSinger().equals(singer))
                .findFirst().orElseThrow(() -> new NoSuchSongException(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND, "No such song!"));
    }

    public void gradeSong(GradeSong gradeSong) throws NoSuchSongException, NotModifyByOwnerException {
        try {
            lock.writeLock().lock();
            Song requiredSong = findSongByName(gradeSong.getSongName(), gradeSong.getSinger());
            if (requiredSong.getUser().equals(gradeSong.getLogin())) {
                throw new NotModifyByOwnerException(HttpStatusCode.CLIENT_ERROR_422_UNPROCESSABLE_CONTENT, "User can't modify defined grade!");
            } else {
                double newAverageGrade = Math.round(
                        (((requiredSong.getAverageGrade() * requiredSong.getRatings().size()) +
                                gradeSong.getGrade()) / (requiredSong.getRatings().size() + 1)) * 10
                ) / 10D;
                requiredSong.getRatings().merge(gradeSong.getLogin(), gradeSong.getGrade(), (key, value) -> value);
                requiredSong.setAverageGrade(newAverageGrade);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void deleteUserGrade(String login) {
        songs.forEach(song -> {
            int size = song.getRatings().size();
            Integer result = song.getRatings().remove(login);
            if (result != null) {
                song.setAverageGrade(Math.round(
                        (((song.getAverageGrade() * size) - result) / song.getRatings().size()) *10
                ) / 10D);
            }
        });
    }

    public List<Song> getConcert() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(songs);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void createComment(Comment comment) throws NoSuchSongException, NotModifyByOwnerException {
        try {
            lock.writeLock().lock();
            Song requiredSong = findSongByName(comment.getSongName(), comment.getSinger());
            boolean sameUser = requiredSong.getComments().stream()
                    .filter(elem -> elem.getId().equals(comment.getHeadComment()))
                    .anyMatch(elem -> elem.getLogin().equals(comment.getLogin()));
            if (sameUser) {
                throw new NotModifyByOwnerException(HttpStatusCode.CLIENT_ERROR_403_FORBIDDEN, "User can't add comments to own comment!");
            }
            requiredSong.getComments().add(comment);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void editComment(Comment comment) throws NoSuchSongException, NoSuchCommentException {
        try {
            lock.writeLock().lock();
            Song requiredSong = findSongByName(comment.getSongName(), comment.getSinger());
            List<Comment> comments = new ArrayList<>();
            requiredSong.getComments()
                    .forEach(elem -> {
                        if (elem.getId().equals(comment.getId()) || elem.getHeadComment().equals(comment.getId())) {
                            comments.add(elem);
                        }
                    });
            Comment requiredComment = comments.stream().filter(elem -> elem.getId().equals(comment.getId()))
                    .findFirst().orElseThrow(() -> new NoSuchCommentException(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND, "No such comment!"));
            if (comments.size() > 1) {
                requiredComment.setLogin("Сообщество радиолюбителей");
                comment.setId(UUID.randomUUID().toString());
                comment.setHeadComment("");
                requiredSong.getComments().add(comment);
            } else {
                requiredComment.setComment(comment.getComment());
                requiredComment.setTime(comment.getTime());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void deleteUserComments(String login) {
        songs.forEach(song -> song.getComments().forEach(comment -> {
            if (comment.getLogin().equals(login)) {
                comment.setLogin("Сообщество радиолюбителей");
            }
        }));
    }

    public void deleteUser(String login) {
        try {
            lock.writeLock().lock();
            users.removeIf(user -> user.getLogin().equals(login));
            deleteUserSong(login);
            deleteUserComments(login);
            deleteUserGrade(login);
            activeTokens.remove(login);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveDatabase(String filePath) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            DataBaseState dataBaseState = new DataBaseState(users, songs, activeTokens);
            stream.writeObject(dataBaseState);
        }
    }

    public synchronized void loadDatabase(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            LOGGER.info("Missing database file on path, creating new");
            file.createNewFile();
            return;
        }
        if (file.length() == 0) {
            LOGGER.info("No data stored in file, nothing to load");
            return;
        }
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
            LOGGER.info("Reading file");
            DataBaseState dataBaseState = (DataBaseState) stream.readObject();
            this.users.addAll(dataBaseState.users);
            this.songs.addAll(dataBaseState.songs);
            this.activeTokens.putAll(dataBaseState.activeTokens);
        }
    }

    private static class DataBaseState implements Serializable {
        List<User> users;
        List<Song> songs;
        Map<String, String> activeTokens;

        public DataBaseState(List<User> users, List<Song> songs, Map<String, String> activeTokens) {
            this.users = users;
            this.songs = songs;
            this.activeTokens = activeTokens;
        }
    }
}
