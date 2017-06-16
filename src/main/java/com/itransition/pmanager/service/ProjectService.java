package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.ProjectDao;
import com.itransition.pmanager.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Lenovo on 12.06.2017.
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;
    public Project findOne(String name){return projectDao.findByName(name);}
    public Project findOne(long id){
        return projectDao.findOne(id);
    }
    public void Save(Project project){
        projectDao.save(project);
    }
    public void Delete(long id){
        projectDao.delete(id);
    }
}
