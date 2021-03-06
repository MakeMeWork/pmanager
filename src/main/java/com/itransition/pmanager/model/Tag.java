package com.itransition.pmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lenovo on 16.06.2017.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int popularity=1;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<Project>(0);

    public Tag(String name, Project project) {
        this.name = name;
        projects = new HashSet<Project>();
        projects.add(project);
    }
}
