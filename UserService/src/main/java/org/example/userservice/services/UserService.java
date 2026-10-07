package org.example.userservice.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.repos.TokenRepo;
import org.example.userservice.repos.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final TokenRepo tokenRepo;
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, TokenRepo tokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
    }

    public User signUp(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(password));
        return userRepo.save(user);
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }

        User user = optionalUser.get();

        if(!passwordEncoder.matches(password, user.getHashedPassword())){
            throw new UsernameNotFoundException("User Email and password are not matching");
        }

        Token token = generateToken(user);
        return tokenRepo.save(token);
    }

    private Token generateToken(User user) {
         Token token = new Token();
         token.setValue(RandomStringUtils.randomAlphabetic(10));
         token.setExpiryAt(System.currentTimeMillis() + 3600000);
         token.setUser(user);
         return token;
    }

    public User validateToken(String tokenValue) {
        Optional<Token> tokenResult =  tokenRepo.findByValueAndDeletedAndExpiryAtGreaterThan(tokenValue,
                false, System.currentTimeMillis());
        if (tokenResult.isEmpty()) {
            return null;
        }
        return tokenResult.get().getUser();
    }
}
