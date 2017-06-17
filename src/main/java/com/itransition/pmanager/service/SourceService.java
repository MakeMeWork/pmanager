package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.SourceDao;
import com.itransition.pmanager.model.Project;
import com.itransition.pmanager.model.Source;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 16.06.2017.
 */
@Service
@RequiredArgsConstructor
public class SourceService {
    private final SourceDao sourceDao;

    public Source findOne(String name){
        return sourceDao.findByFilename(name);
    }

    public List<Source> findByProject(Project project){
        return sourceDao.findByProject(project);
    }

    public void Save(Source source){
        Source entity = sourceDao.findByFilename(source.getFilename());
        if(Objects.nonNull(entity)){
            entity.setFilename(source.getFilename());
            entity.setLink(source.getLink());
            sourceDao.save(entity);
        }else{
            sourceDao.save(source);
        }

    }
}
