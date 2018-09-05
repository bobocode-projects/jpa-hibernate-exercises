package com.bobocode.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Employee {
    private Long id;
    private String email;
    private String fistName;
    private String lastName;
}
