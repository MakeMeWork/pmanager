package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.TagDao;
import com.itransition.pmanager.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by Lenovo on 16.06.2017.
 */
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagDao tagDao;

    public Tag findOne(String name){
        return tagDao.findByName(name);
    }

    private void Save(Tag tag){
        Tag entity = tagDao.findByName(tag.getName());
        if(Objects.nonNull(entity)){
            entity.setPopularity(entity.getPopularity()+1);
            tagDao.save(entity);
        }
        else{
            tagDao.save(tag);
        }
    }
}
