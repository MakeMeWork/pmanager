package com.itransition.pmanager.service;

import com.itransition.pmanager.dao.NewsDao;
import com.itransition.pmanager.model.News;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lenovo on 17.06.2017.
 */
@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsDao newsDao;

    public News findByName(String name){
        return newsDao.findByName(name);
    }

    public List<News> findAll(){
        return newsDao.findAll();
    }

    public void save(News news){
        News entity = newsDao.findByName(news.getName());
        if(Objects.nonNull(entity)){
            entity.setContent(news.getContent());
            entity.setLink(news.getLink());
            newsDao.save(entity);
        }else{ newsDao.save(news);}
    }
    public List<News> findAllsorted(){
        return  newsDao.findAll();
    }
    public List<News> findFiveNews(){
        List<News> news = newsDao.findAll();
        List<News> rnews = new ArrayList<News>();
        for (int i = 0; (i < 5)&(i<news.size()); i++){
            if (Objects.nonNull(news.get(i))){
                rnews.add(news.get(i));
            }else{break;}
        }
        return rnews;
    }

}
