package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.Project;
import com.itransition.pmanager.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Lenovo on 16.06.2017.
 */
public interface SourceDao extends JpaRepository<Source, Integer> {
    public Source findByFilename(@Param("filename")String filename);

    public List<Source> findByProject(@Param("project")Project project);
}
