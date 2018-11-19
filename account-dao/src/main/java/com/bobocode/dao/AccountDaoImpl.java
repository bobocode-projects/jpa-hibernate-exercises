package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(account);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Can not save this entity", e.getCause());
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Account findById(Long id) {
        EntityManager entityManager = getEntityManager(emf);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account account = entityManager.find(Account.class, id);
        transaction.commit();
        entityManager.close();
        return account;
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager entityManager = getEntityManager(emf);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account account = (Account) entityManager.createQuery("select a from Account a where a.email = :email")
                .setParameter("email", email)
                .getSingleResult();
        transaction.commit();
        entityManager.close();
        return account;
    }

    @Override
    public List<Account> findAll() {
        EntityManager entityManager = getEntityManager(emf);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        List<Account> list = (List<Account>) entityManager
                .createQuery("select a from Account a", Account.class)
                .getResultList();
        transaction.commit();
        entityManager.close();
        return list;
    }

    @Override
    public void update(Account account) {
        EntityManager entityManager = getEntityManager(emf);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.merge(account);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Can not update entity", e.getCause());
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void remove(Account account) {
        EntityManager entityManager = getEntityManager(emf);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account accountToRemove = entityManager.merge(account);
        entityManager.remove(accountToRemove);
        transaction.commit();
        entityManager.close();
    }


    private EntityManager getEntityManager(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }
}

