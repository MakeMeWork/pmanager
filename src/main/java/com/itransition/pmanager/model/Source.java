package com.itransition.pmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Lenovo on 16.06.2017.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String filename;
    private String link;
    @ManyToOne
    private Project project;

    public Source(String filename, String link, Project project) {
        this.filename = filename;
        this.link = link;
        this.project = project;
    }
}
