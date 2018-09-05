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
        entityManager.getTransaction().begin(); //WITHOUT HAVE A TRANSACTION
        try {
            entityManager.persist(account);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new AccountDaoException("Failed to save account", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Account findById(Long id) {
        EntityManager entityManager = emf.createEntityManager(); //HAVING TRANSACTION
        EntityTransaction transaction = entityManager.getTransaction();//TODO: do we need transaction? I guess no
        transaction.begin();

        try {
            return entityManager.find(Account.class, id);
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Failed to save account", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Account findByEmail(String email) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            return entityManager.createQuery("Select a from Account a where a.email = :email", Account.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            //TODO: just log?
            throw new RuntimeException("Failed to get account by email: " + email, e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Account> findAll() {
        EntityManager entityManager = emf.createEntityManager();
        return entityManager.createQuery("select a from Account a", Account.class).getResultList();
    }

    @Override
    public void update(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            Account managedAccount = entityManager.find(Account.class, account.getId());
            managedAccount.setBalance(account.getBalance());
            managedAccount.setBirthday(account.getBirthday());
            managedAccount.setCreationTime(account.getCreationTime());
            managedAccount.setEmail(account.getEmail());
            managedAccount.setFirstName(account.getFirstName());
            managedAccount.setLastName(account.getLastName());
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Failed to update account", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void remove(Account account) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            Account managedAccount = entityManager.find(Account.class, account.getId());// OR MERGE and REMOVE
            entityManager.remove(managedAccount);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new AccountDaoException("Failed to remove account", e);
        } finally {
            entityManager.close();
        }
    }
}

