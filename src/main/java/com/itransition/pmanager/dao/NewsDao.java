package com.itransition.pmanager.dao;

import com.itransition.pmanager.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by Lenovo on 17.06.2017.
 */
public interface NewsDao extends JpaRepository<News, Integer> {
    public News findByName(@Param("name") String name);
    //public List<News> findAllOrderByDate();
}
