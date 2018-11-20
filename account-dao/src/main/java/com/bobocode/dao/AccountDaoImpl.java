package com.bobocode.dao;

import com.bobocode.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Account foundAccount = em.find(Account.class, id);
        em.close();
        return foundAccount;
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.close();
        return null;
    }

    @Override
    public List<Account> findAll() {
        throw new UnsupportedOperationException("I don't wanna work without implementation!"); // todo
    }

    @Override
    public void update(Account account) {
        throw new UnsupportedOperationException("I don't wanna work without implementation!"); // todo
    }

    @Override
    public void remove(Account account) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(account));
        em.getTransaction().commit();
        em.close();
    }
}

