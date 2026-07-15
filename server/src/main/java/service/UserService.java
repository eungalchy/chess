package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import javax.xml.crypto.Data;
import java.util.UUID;
import model.LoginRequest;
import model.LoginResult;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        UserData existing = dataAccess.getUser(request.username());
        if (existing!=null) {
            throw new DataAccessException("Error: already taken");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);
        return new LoginResult(request.username(), authToken);
    }
}
