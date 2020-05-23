package com.bobocode.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * todo:
 * - implement no argument constructor
 * - implement getters and setters
 * - make a setter for field {@link Photo#comments} {@code private}
 * - implement equals() and hashCode() based on identifier field
 * <p>
 * - configure JPA entity
 * - specify table name: "photo"
 * - configure auto generated identifier
 * - configure not nullable and unique column: url
 * <p>
 * - initialize field comments
 * - map relation between Photo and PhotoComment on the child side
 * - implement helper methods {@link Photo#addComment(PhotoComment)} and {@link Photo#removeComment(PhotoComment)}
 * - enable cascade type {@link javax.persistence.CascadeType#ALL} for field {@link Photo#comments}
 * - enable orphan removal
 */
@Getter
@Setter
@Entity
@Table(name = "photo")
@EqualsAndHashCode(of = "id")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String url;
    @Column
    private String description;
    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoComment> comments = new ArrayList<>();

    private void setComments(List<PhotoComment> comments) {
        comments.clear();
    }

    public void addComment(PhotoComment comment) {
        Objects.requireNonNull(comment);
        comment.setPhoto(this);
        comments.add(comment);
    }

    public void removeComment(PhotoComment comment) {
        Objects.requireNonNull(comment);
        comments.remove(comment);
        comment.setPhoto(null);
    }

}
