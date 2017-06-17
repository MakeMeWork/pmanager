package com.itransition.pmanager.model;

import javax.persistence.*;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String content;
    @ManyToOne
    private Project project;
}
