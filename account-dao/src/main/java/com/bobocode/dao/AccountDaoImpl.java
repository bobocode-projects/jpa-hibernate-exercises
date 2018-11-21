package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountDaoImpl implements AccountDao {
    private static EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
//        EntityManager entityManager = emf.createEntityManager();
//        entityManager.getTransaction().begin();
//        entityManager.persist(account);
//        entityManager.getTransaction().commit();
//        entityManager.close();
        performOperationWithoutReturnData(entityManager -> entityManager.persist(account));
    }

    @Override
    public Account findById(Long id) {
        return performOperationWithReturnData(entityManager ->
                entityManager.createQuery("select a from Account a where a.id = :id", Account.class)
                        .setParameter("id", id)
                        .getSingleResult());
    }

    @Override
    public Account findByEmail(String email) {
        return performOperationWithReturnData(entityManager ->
                entityManager.createQuery("select a from Account a where a.email = :email", Account.class)
                        .setParameter("email", email)
                        .getSingleResult());
    }

    @Override
    public List<Account> findAll() {
        return performOperationWithReturnData(entityManager ->
                entityManager.createQuery("select a from Account a", Account.class)
                        .getResultList());
    }

    @Override
    public void update(Account account) {
        performOperationWithoutReturnData(entityManager ->
                entityManager.merge(account));
    }

    @Override
    public void remove(Account account) {
//        performOperationWithoutReturnData(entityManager ->
//        {
//            Account removeAccount = entityManager.merge(account);
//            entityManager.remove(removeAccount);
//        });
        performOperationWithoutReturnData(entityManager -> {
            Account removeAccount = entityManager.find(Account.class, account.getId());
            entityManager.remove(removeAccount);
        });
    }


    public static void performOperationWithoutReturnData(Consumer<EntityManager> consumer) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            consumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Transaction rejected", e);
        } finally {
            entityManager.close();
        }
    }

    private static <T> T performOperationWithReturnData(Function<EntityManager, T> function) {
        EntityManager entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        T result;
        try {
            result = function.apply(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new AccountDaoException("Transaction rejected", e);
        } finally {
            entityManager.close();
        }
        return result;
    }
}

