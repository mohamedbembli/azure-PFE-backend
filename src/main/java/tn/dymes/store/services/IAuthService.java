package tn.dymes.store.services;

import java.util.Map;

public interface IAuthService {
    Map<String, String> generateToken(String email, boolean withRefreshToken);
    void sendActivationCode(String email);
    String authorizePasswordInitialization(String authorizationCode, String email);

    void resetPassword(String pass, String email);

}
