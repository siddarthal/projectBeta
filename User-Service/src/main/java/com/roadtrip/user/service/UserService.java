package com.roadtrip.user.service;
import com.roadtrip.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    public User rgstrUsr(User user);

    public User upDtprfrncs(String email, Map<String, Object> preferences);

    public String lgnUsr(String email,String password);
}
