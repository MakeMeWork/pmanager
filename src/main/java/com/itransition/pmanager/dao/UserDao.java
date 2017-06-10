package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Lenovo on 03.06.2017.
 */
public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(@Param("username") String username);
}
