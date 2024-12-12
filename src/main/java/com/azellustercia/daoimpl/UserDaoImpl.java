package com.azellustercia.daoimpl;

import com.azellustercia.dao.UserDao;
import com.azellustercia.database.Database;
import com.azellustercia.exceptions.user.RegisterException;
import com.azellustercia.model.User;

public class UserDaoImpl implements UserDao {

    @Override
    public void create(User user) throws RegisterException {
        Database.getInstance().addUser(user);
    }

    @Override
    public boolean checkExists(String login) {
        return Database.getInstance().checkExists(login);
    }

    @Override
    public boolean authorization(String login, String password) {
        return Database.getInstance().authorization(login, password);
    }

    @Override
    public boolean checkActiveSession(String login) {
        return Database.getInstance().checkActiveSession(login);
    }

    @Override
    public boolean checkActiveSessionToken(String token) {
        return Database.getInstance().checkActiveSessionToken(token);
    }

    @Override
    public void addActiveSession(String login, String token) {
        Database.getInstance().addToken(login, token);
    }

    @Override
    public void logOut(String login) {
        Database.getInstance().removeToken(login);
    }

    @Override
    public String getLoginByToken(String token) {
        return Database.getInstance().getLoginByToken(token);
    }

    @Override
    public void addUserSession(String login, String data) throws RegisterException {
        Database.getInstance().addUserSession(login, data);
    }

    @Override
    public void deleteUser(String login) {
        Database.getInstance().deleteUser(login);
    }
}
