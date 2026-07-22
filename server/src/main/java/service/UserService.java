package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData existing = dataAccess.getUser(request.username());
        if (existing!=null) {
            throw new DataAccessException("Error: already taken");
        }
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(request.password(), org.mindrot.jbcrypt.BCrypt.gensalt());
        UserData user = new UserData(request.username(), hashed, request.email());
        dataAccess.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(request.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);
        return new LoginResult(request.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }
}
