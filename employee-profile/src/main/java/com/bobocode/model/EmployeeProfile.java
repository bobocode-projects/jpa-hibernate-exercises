package com.bobocode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * todo:
 * - implement not argument constructor
 * - implement getters and setters
 * - implement equals and hashCode based on identifier field
 *
 * - configure JPA entity
 * - specify table name: "employee_profile"
 * - configure not nullable columns: position, department
 *
 * - map relation between {@link Employee} and {@link EmployeeProfile} using foreign_key column: "employee_id"
 * - configure a derived identifier. E.g. map "employee_id" column should be also a primary key (id) for this entity
 * - configure relation as mandatory (not optional)
 */
@NoArgsConstructor
@Getter
@Setter
public class EmployeeProfile {
    private Long id;
    private Employee employee;
    private String position;
    private String department;
}
