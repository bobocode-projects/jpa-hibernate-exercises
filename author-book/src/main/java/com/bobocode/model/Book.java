package com.bobocode.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * todo:
 * - implement no arguments constructor
 * - implement getters and setters for all fields
 * - implement equals() and hashCode() based on {@link Book#isbn}
 * - make setter for field {@link Book#authors} private
 * - initialize field {@link Book#authors} as new {@link HashSet}
 * <p>
 * - configure JPA entity
 * - specify table name: "book"
 * - configure auto generated identifier
 * - configure mandatory column "name" for field {@link Book#name}
 * - configure mandatory unique column "isbn" for field {@link Book#isbn}, it is a natural key candidate
 * <p>
 * - configure many-to-many relation as mapped on the {@link Author} side
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "book")
@EqualsAndHashCode(of = "isbn")
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @NaturalId
    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();

    private void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }
}
