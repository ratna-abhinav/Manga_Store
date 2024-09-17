package com.example.shoppingdotcom.service.impl;

import com.example.shoppingdotcom.model.UserDtls;
import com.example.shoppingdotcom.repository.UserRepository;
import com.example.shoppingdotcom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDtls saveUser(UserDtls user) {
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }
}
