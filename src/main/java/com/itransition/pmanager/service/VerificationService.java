package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.VerificationDao;
import com.itransition.pmanager.model.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by Lenovo on 14.06.2017.
 */
@Service
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationDao verificationDao;

    public void save(Verification verification){
        verificationDao.save(verification);
    }

    public Verification findOne(String link){
        return verificationDao.findByLink(link);
    }
}
