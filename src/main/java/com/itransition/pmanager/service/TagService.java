package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.TagDao;
import com.itransition.pmanager.model.Project;
import com.itransition.pmanager.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    public void Save(Tag tag){
        Tag entity = tagDao.findByName(tag.getName());
        saveEntity(tag, entity);
    }

    private Tag saveEntity(Tag tag, Tag entity) {
        if(Objects.nonNull(entity)){
            entity.setPopularity(entity.getPopularity()+1);
            entity.setProjects(tag.getProjects());
            tagDao.save(entity);
            return  entity;
        }
        else{
            tagDao.save(tag);
            return tag;
        }
    }

    public List<Tag> Save (String[] tags, Project project){
        List<Tag> tagSet = new ArrayList<Tag>();
        for (String tag: tags
             ) {
            Tag entity = tagDao.findByName(tag);
            Tag baseTag = new Tag(tag, project);
            tagSet.add(saveEntity(baseTag, entity));
        }
        return tagSet;
    }
    public List<Tag> findSevenTags(){
        List<Tag> news = tagDao.findAll();
        news.sort(new Comparator<Tag>() {
            @Override
            public int compare(Tag o1, Tag o2) {
                return  o2.getPopularity() > o1.getPopularity() ? 1 : o2.getPopularity() < o1.getPopularity() ? -1 : 0;
            }
        });
        List<Tag> rnews = new ArrayList<Tag>();
        for (int i = 0; (i < 7)&(i<news.size()); i++){
            if (Objects.nonNull(news.get(i))){
                rnews.add(news.get(i));
            }else{break;}
        }
        return rnews;
    }


}
