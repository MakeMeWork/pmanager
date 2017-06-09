package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.UserDao;
import com.itransition.pmanager.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Lenovo on 09.06.2017.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void update(User user) {
//        User entity = userDao.findOne(user.getId());
//
//        if (Objects.nonNull(entity)) {
//            entity.setUsername(user.getUsername());
//            entity.setEmail(user.getEmail());
//            entity.setRole(user.getRole());
//
//            if (Objects.nonNull(user.getPassword())) {
//                entity.setPassword(user.getPassword());
//            }
//
//            userDao.save(entity);
//        }
        userDao.save(user);
    }

}
