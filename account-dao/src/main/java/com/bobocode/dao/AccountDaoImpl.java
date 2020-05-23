package com.bobocode.dao;

import com.bobocode.exception.AccountDaoException;
import com.bobocode.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AccountDaoImpl implements AccountDao {
    private EntityManagerFactory emf;

    public AccountDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Account account) {
        performWithinTransaction(em -> em.persist(account));
    }

    @Override
    public Account findById(Long id) {
        return returnWithinTransaction(entityManager -> entityManager.find(Account.class, id));
    }

    @Override
    public Account findByEmail(String email) {
        return returnWithinTransaction(entityManager ->
                entityManager.createQuery("SELECT a FROM Account a where email= :email", Account.class)
                    .setParameter("email", email)
                    .getSingleResult()
        );
    }

    @Override
    public List<Account> findAll() {
        return returnWithinTransaction(entityManager ->
            entityManager.createQuery("SELECT a FROM Account a", Account.class).getResultList());

    }

    @Override
    public void update(Account account) {
        performWithinTransaction(entityManager -> entityManager.merge(account));
    }

    @Override
    public void remove(Account account) {
        performWithinTransaction(em -> {
            Account account1 = em.find(Account.class, account.getId());
            if (account1 != null) {
                em.remove(account1);
            }
        });
    }

    private EntityManager getEntityManager() {
        return this.emf.createEntityManager();
    }

    private <T> T returnWithinTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = null;
        try {
            entityManager = getEntityManager();
            entityManager.getTransaction().begin();
            T result = function.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new AccountDaoException("Error while saving account", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    private void performWithinTransaction(Consumer<EntityManager> consumer) {
        EntityManager entityManager = null;
        try {
            entityManager = getEntityManager();
            entityManager.getTransaction().begin();
            consumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new AccountDaoException("Error performing JPA operation. Transaction is rolled back", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}

