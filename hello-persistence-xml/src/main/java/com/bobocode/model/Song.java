package com.bobocode.model;

import javax.persistence.*;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private String author;
}
