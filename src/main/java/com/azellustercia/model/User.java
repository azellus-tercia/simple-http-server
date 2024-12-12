package com.azellustercia.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private final String login;
    private final String password;
    private final String name;
    private final String surname;
    private final List<String> sessionHistory;

    public User(String login, String password, String name, String surname, List<String> sessionHistory) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.sessionHistory = sessionHistory;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<String> getSessionHistory() {
        return sessionHistory;
    }
}
