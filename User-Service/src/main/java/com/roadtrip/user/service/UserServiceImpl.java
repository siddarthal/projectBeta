package com.roadtrip.user.service;

import com.roadtrip.user.entity.User;
import com.roadtrip.user.repo.UserRepository;
import com.roadtrip.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;


public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    JwtUtil jwt;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User rgstrUsr(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            // handle exception here and return it
            throw new RuntimeException();

        }
        //HASH THE PASSWORD BEFORE SAVING IT
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);

    }

    @Override
    public User upDtprfrncs(String email, Map<String, Object> preferences) {
        Optional<User> userPref = repository.findByEmail(email);
        if (userPref.isPresent()) {
            User pref = userPref.get();
            pref.setPreferences(preferences);
            return repository.save(pref);
        }
        return null;
    }

    @Override
    public String lgnUsr(String email, String password) {
        Optional<User> userCred = repository.findByEmail(email);
        if (userCred.isPresent() && passwordEncoder.matches(password, userCred.get().getPassword())) {
            return jwt.generateToken(email);
        }
        return "";
    }
}
