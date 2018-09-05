package com.bobocode.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * todo:
 * - implement not argument constructor
 * - implement getters and setters
 * - implement equals and hashCode based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "photo"
 * - configure auto generated identifier
 * - configure not nullable columns: url, description
 *
 * - initialize field comments
 * - map relation between Photo and PhotoComment on the child side
 */
@Getter
@Setter
public class Photo {
    private Long id;
    private String url;
    private String description;
    private List<PhotoComment> comments;
}
