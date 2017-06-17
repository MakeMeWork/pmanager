package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.SettingsDao;
import com.itransition.pmanager.model.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 15.06.2017.
 */
@Service
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsDao settingsDao;

    public void save(Settings settings){
        Settings entity = settingsDao.findByMykey(settings.getMykey());
        if (Objects.nonNull(entity)){
            entity.setMyvalue(settings.getMyvalue());
            settingsDao.save(entity);
        }else{
            settingsDao.save(settings);
        }

    }
    public List<Settings> findAll(){
        return settingsDao.findAll();
    }
    public String getProperty(String key){
        if (Objects.nonNull(settingsDao.findByMykey(key))) {
            return settingsDao.findByMykey(key).getMyvalue();
        }
        return "";
    }
}
