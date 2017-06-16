package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Lenovo on 12.06.2017.
 */
public interface ProjectDao extends JpaRepository<Project, Long> {
    Project findFirstById(long Id);
    Project findByName(@Param("name") String name);
}
