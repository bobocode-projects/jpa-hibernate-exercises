package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Account findById(Long id) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Account foundAccount = entityManager.find(Account.class, id);
        entityManager.close();
        return foundAccount;
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        TypedQuery<Account> findByEmailQuery = entityManager
                .createQuery("SELECT a FROM Account a WHERE a.email = :email", Account.class);
        findByEmailQuery.setParameter("email", email);
        return findByEmailQuery.getSingleResult();
    }

    @Override
    public List<Account> findAll() {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        List<Account> accounts = entityManager
                .createQuery("select a from Account a", Account.class).getResultList();
        entityManager.close();
        return accounts;
    }

    @Override
    public void update(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(account);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void remove(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        Account mergedAccount = entityManager.merge(account);
        entityManager.remove(mergedAccount);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}

