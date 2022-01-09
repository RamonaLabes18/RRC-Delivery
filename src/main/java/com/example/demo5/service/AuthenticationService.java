package com.example.demo5.service;

import com.example.demo5.exceptions.AuthenticationFailException;
import com.example.demo5.models.AuthenticationToken;
import com.example.demo5.models.User;
import com.example.demo5.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    TokenRepository tokenRepository;

    public void saveConfirmationToken(AuthenticationToken authenticationToken) {
        tokenRepository.save(authenticationToken);
    }

    public AuthenticationToken getToken(User user) {
        return tokenRepository.findTokenByUser(user);
    }

    public User getUser(String token) {
        AuthenticationToken authenticationToken = tokenRepository.findTokenByToken(token);
        if (authenticationToken != null) {
            if (authenticationToken.getUser() != null) {
                return authenticationToken.getUser();
            }
        }
        return null;
    }

    //se verifica daca token-ul care vine de pe frontend este valid sau daca este prezent
    public void authenticate(String token) throws AuthenticationFailException {
        if (token == null) {
            throw new AuthenticationFailException("AUTH_TOEKN_NOT_PRESENT");
        }
        if (getUser(token) == null) {
            throw new AuthenticationFailException("AUTH_TOEKN_NOT_VALID");
        }
    }
}
