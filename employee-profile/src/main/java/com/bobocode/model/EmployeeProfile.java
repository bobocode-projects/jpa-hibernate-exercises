package com.bobocode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmployeeProfile {
    private Long id;
    private Employee employee;
    private String position;
    private String department;
}
