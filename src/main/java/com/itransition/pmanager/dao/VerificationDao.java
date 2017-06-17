package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Lenovo on 14.06.2017.
 */
public interface VerificationDao extends JpaRepository<Verification, Integer> {
    public Verification findByLink(@Param("link") String link);
}
