package com.itransition.pmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description = "";
    @ManyToMany(fetch = FetchType.LAZY)
    private List<User> users;
    @Enumerated(EnumType.STRING)
    private State state = State.Open;



}
