package com.itransition.pmanager.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Entity
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private Project project;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Message> messages;

}
