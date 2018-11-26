package com.bobocode.dao;

import javax.persistence.EntityManagerFactory;

import com.bobocode.entity.User;

public class UserDao extends AbstractCrudDao<User> {
    public UserDao(EntityManagerFactory emf) {
        super(emf);
        entityClass = User.class;
    }
}
