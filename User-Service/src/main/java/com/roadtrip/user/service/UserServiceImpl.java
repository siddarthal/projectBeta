package com.roadtrip.user.service;

import com.roadtrip.user.dto.LoginRequest;
import com.roadtrip.user.entity.CustomUserDetails;
import com.roadtrip.user.entity.User;
import com.roadtrip.user.exception.*;
import com.roadtrip.user.repo.UserRepository;
import com.roadtrip.user.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    UserRepository repository;

    @Autowired
    JwtUtil jwt;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User rgstrUsr(User user) {
        logger.info("Registering new user with email: {}", user.getEmail());
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException(user.getEmail());
        }
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = repository.save(user);
            logger.info("saved  user with email: {}", savedUser.getEmail());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error registering user", e);
            throw new UserServiceException("Error registering user: " + e.getMessage(), e);
        }
    }

    @Override
    public User upDtprfrncs(String email, Map<String, Object> preferences) {
        logger.info("Updating preferences for user: {}", email);
        try {
            Optional<User> userPref = repository.findByEmail(email);
            if (userPref.isPresent()) {
                User pref = userPref.get();
                pref.setPreferences(preferences);
                logger.info("Preferences updated for user with email {}", email);
                return repository.save(pref);
            } else {
                throw new UserNotFoundException(email);
            }
        } catch (UserNotFoundException e) {
            logger.error("User not found with email {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while updating preferences");
            throw new PreferenceUpdateException(e.getMessage(), e);
        }
    }

    @Override
    public String lgnUsr(LoginRequest request) {
        String email = request.getEmail();
        logger.info("Login attempt by user {}", email);
        try {
            Optional<User> userCred = repository.findByEmail(email);
            if (userCred.isPresent() && passwordEncoder.matches(request.getPassword(), userCred.get().getPassword())) {
                return jwt.generateToken(email);
            } else {
                throw new InvalidCredentialsException(email);
            }
        } catch (InvalidCredentialsException e) {
            logger.info("User trying to access with invalid credentials");
            throw e;
        } catch (Exception e) {
            logger.error("Error during login for user: {}", email, e);
            throw new UserServiceException("Login process failed: " + e.getMessage(), e);
        }
    }

    @Override
    public CustomUserDetails loadUserByUsername(String email) {
        logger.info("Loading user details for: {}", email);

        Optional<User> user = repository.findByEmail(email);
        if (user.isPresent()) {
            logger.info("User details loaded successfully for: {}", email);
            return new CustomUserDetails(user.get());
        }
        logger.warn("User not found while loading details: {}", email);
        throw new UserNotFoundException(email);
    }
}
