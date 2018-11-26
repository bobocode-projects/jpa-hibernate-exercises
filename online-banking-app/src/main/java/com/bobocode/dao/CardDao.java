package com.bobocode.dao;

import javax.persistence.EntityManagerFactory;

import com.bobocode.entity.Card;

public class CardDao extends AbstractCrudDao<Card> {
    public CardDao(EntityManagerFactory emf) {
        super(emf);
        entityClass = Card.class;
    }
}
