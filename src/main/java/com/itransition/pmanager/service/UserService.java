package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.UserDao;
import com.itransition.pmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public User findOne(String name){
        return userDao.findByUsername(name);
    }
    public void save(User user) {
        userDao.save(user);
    }
    public void update(User entity,User user) {
        if (Objects.nonNull(entity)) {
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            entity.setEmail(user.getEmail());
            if (Objects.nonNull(user.getRole())){
                entity.setRole(user.getRole());
            }
            if (user.getPassword()!= "") {
                entity.setPassword(user.getPassword());
            }
            userDao.save(entity);
        }
    }
    public List<User> findAll(){
        return userDao.findAll();
    }

}
