package com.bobocode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * todo:
 * - implement no argument constructor
 * - implement getters and setters
 * - implement equals and hashCode based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "photo_comment"
 * - configure auto generated identifier
 * - configure not nullable column: text
 *
 * - map relation between Photo and PhotoComment using foreign_key column: "photo_id"
 * - configure relation as mandatory (not optional)
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "photo_comment")
public class PhotoComment {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column
    private LocalDateTime createdOn;

    @JoinColumn(name = "photo_id")
    @ManyToOne
    private Photo photo;
}
