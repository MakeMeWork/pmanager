package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Lenovo on 16.06.2017.
 */
public interface TagDao extends JpaRepository<Tag, Integer> {
    public Tag findByName(@Param("name") String name);
    //public Set<Tag> findByProjects(@Param("projects")Project projects);
}
