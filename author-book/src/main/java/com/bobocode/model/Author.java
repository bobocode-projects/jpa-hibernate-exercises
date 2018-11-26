package com.bobocode.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * todo:
 * - implement no arguments constructor
 * - implement getters and setters for all fields
 * - implement equals() based on identifier field
 * - implement hashCode() that return constant value 31
 * - make setter for field {@link Author#books} private
 * - initialize field {@link Author#books} as new {@link HashSet}
 * - implement a helper {@link Author#addBook(Book)} that establishes a relation on both sides
 * - implement a helper {@link Author#removeBook(Book)} that drops a relation on both sides
 * <p>
 * - configure JPA entity
 * - specify table name: "author"
 * - configure auto generated identifier
 * - configure mandatory column "first_name" for field {@link Author#firstName}
 * - configure mandatory column "last_name" for field {@link Author#lastName}
 * <p>
 * - configure many-to-many relation between {@link Author} and {@link Book}
 * - configure cascade operations for this relations {@link CascadeType#PERSIST} and {@link CascadeType#MERGE}
 * - configure link (join) table "author_book"
 * - configure foreign key column "book_id" references book table
 * - configure foreign key column "author_id" references author table
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Author {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JoinTable(name = "author_book",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter(AccessLevel.PRIVATE)
    private Set<Book> books = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void addBook(Book book) {
        books.add(book);
        Set<Author> authors = book.getAuthors();
        authors.add(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        Set<Author> authors = book.getAuthors();
        authors.remove(this);
    }


}
