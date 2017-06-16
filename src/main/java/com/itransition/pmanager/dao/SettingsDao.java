package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Lenovo on 15.06.2017.
 */
public interface SettingsDao extends JpaRepository<Settings, String> {
    public Settings findByMykey(@Param("mykey") String key);
}
