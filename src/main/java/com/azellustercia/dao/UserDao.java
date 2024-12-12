package com.azellustercia.dao;

import com.azellustercia.exceptions.user.RegisterException;
import com.azellustercia.model.User;

public interface UserDao {
    void create(User user) throws RegisterException;
    boolean checkExists(String login);
    boolean authorization(String login, String password);
    boolean checkActiveSession(String login);
    boolean checkActiveSessionToken(String token);
    void addActiveSession(String login, String token);
    void logOut(String token);
    String getLoginByToken(String token);
    void addUserSession(String login, String data) throws RegisterException;
    void deleteUser(String login);
}
