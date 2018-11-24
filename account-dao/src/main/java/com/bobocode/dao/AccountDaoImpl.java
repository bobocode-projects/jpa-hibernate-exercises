package com.bobocode.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        executeEntityManagerRequest(entityManager -> entityManager.persist(account));
    }

    @Override
    public Account findById(Long id) {
        return resultListFromEntityManagerRequest(entityManager -> entityManager.find(Account.class, id));
    }

    @Override
    public Account findByEmail(String email) {
        return resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("select a from Account a where a.email=:email", Account.class)
                .setParameter("email", email)
                .getResultList()
                .get(0));
    }

    @Override
    public List<Account> findAll() {
        return resultListFromEntityManagerRequest(entityManager -> entityManager
                .createQuery("from Account ", Account.class)
                .getResultList());
    }

    @Override
    public void update(Account account) {
        executeEntityManagerRequest(entityManager -> entityManager.merge(account));
    }

    @Override
    public void remove(Account account) {
        executeEntityManagerRequest(entityManager -> {
            Account mergedAccount = entityManager.merge(account);
            entityManager.remove(mergedAccount);
        });
    }

    private void executeEntityManagerRequest(Consumer<EntityManager> emConsumer) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            emConsumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("EntityManager request wasn't executed!", e.getCause());
        } finally {
            entityManager.close();
        }
    }

    private <T> T resultListFromEntityManagerRequest(Function<EntityManager, T> emFunction) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            T resultList = emFunction.apply(entityManager);
            return resultList;
        } catch (Exception e) {
            throw new AccountDaoException("EntityManager request wasn't executed!", e.getCause());
        } finally {
            entityManager.close();
        }
    }
}

