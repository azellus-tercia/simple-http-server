package com.azellustercia.service;

import com.azellustercia.dao.UserDao;
import com.azellustercia.daoimpl.UserDaoImpl;
import com.azellustercia.dto.request.user.DeleteUserRequest;
import com.azellustercia.dto.request.user.LoginRequest;
import com.azellustercia.dto.request.user.RegisterUserRequest;
import com.azellustercia.exceptions.user.*;
import com.azellustercia.http.headers.HttpStatusCode;
import com.azellustercia.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDaoImpl();

    public void create(RegisterUserRequest registerUserRequest) throws RegisterException {
        userDao.create(new User(
                registerUserRequest.getLogin(),
                registerUserRequest.getPassword(),
                registerUserRequest.getName(),
                registerUserRequest.getSurname(),
                new ArrayList<>()
        ));
    }

    public String authorization(LoginRequest loginRequest) throws RegisterException, WrongPasswordException, AlreadyAuthorizedException {
        String token = "";
        if (!userDao.checkExists(loginRequest.getLogin())) {
            throw new RegisterException(HttpStatusCode.CLIENT_ERROR_409_CONFLICT, "User is not exists!");
        } else if (!userDao.authorization(loginRequest.getLogin(), loginRequest.getPassword())) {
            throw new WrongPasswordException(HttpStatusCode.CLIENT_ERROR_403_FORBIDDEN, "Wrong password!");
        } else if (userDao.checkActiveSession(loginRequest.getLogin())) {
            throw new AlreadyAuthorizedException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, "User is already authorized!");
        } else {
            token = UUID.randomUUID().toString();
            userDao.addActiveSession(loginRequest.getLogin(), token);
            userDao.addUserSession(loginRequest.getLogin(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.ss")));
        }
        return token;
    }

    public void logOut(String token) throws SessionNotExistsException {
        if (userDao.checkActiveSessionToken(token)) {
            userDao.logOut(userDao.getLoginByToken(token));
        } else {
            throw new SessionNotExistsException(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED, "Session is not existed!");
        }
    }

    public String getLoginByToken(String token) throws SessionNotExistsException {
        if (!userDao.checkActiveSessionToken(token)) {
            throw new SessionNotExistsException(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED, "Session is not existed!");
        }
        return userDao.getLoginByToken(token);
    }

    public void deleteUser(DeleteUserRequest request, String login) throws NotAllowedToProceedException {
        if (!request.getLogin().equals(login)) {
            throw new NotAllowedToProceedException(HttpStatusCode.CLIENT_ERROR_403_FORBIDDEN, "User can't delete another user!");
        }
        userDao.deleteUser(request.getLogin());
    }
}
