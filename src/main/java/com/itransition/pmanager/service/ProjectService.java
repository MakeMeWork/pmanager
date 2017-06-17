package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.ProjectDao;
import com.itransition.pmanager.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 12.06.2017.
 */
@Service
public class ProjectService {
    @Autowired
    private ProjectDao projectDao;

    public Project findOne(String name){return projectDao.findByName(name);}
    public Project findOne(int id){
        return projectDao.findOne(id);
    }
    public void Save(Project project){
        projectDao.save(project);
    }
    public void Delete(int id){
        projectDao.delete(id);
    }
    public List<Project> findAll(){
        return projectDao.findAll();
    }
    public List<Project> findFiveProjects(){
        return findFiveProjects(projectDao.findAll());
    }
    public List<Project> findFiveProjects(List<Project> projects){
        List<Project> rprojects = new ArrayList<Project>(   );
        for (int i = 0; (i < 5)&(i<projects.size()); i++){
            if (Objects.nonNull(projects.get(i))){
                rprojects.add(projects.get(i));
            }else{break;}
        }
        return (List<Project>) rprojects;
    }
}
