package com.itransition.pmanager.model;

import lombok.Getter;
import lombok.Setter;
import ys.wikiparser.WikiParser;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Entity
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String description = "";
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<User>(0);
    @Enumerated(EnumType.STRING)
    private State state = State.Open;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Tag> tags = new ArrayList<>(0);
    @OneToMany
    private List<Source> sources = new ArrayList<Source>(0);
    @OneToMany
    private List<Message> messages = new ArrayList<Message>(0);
    private String wiki="";

    public Project(){
        users = new ArrayList<User>();
        tags = new ArrayList<Tag>();
        sources = new ArrayList<Source>();
        messages = new ArrayList<Message>();
    }

    public String wikiCompile(){
        return WikiParser.renderXHTML(this.wiki);
    }

}
