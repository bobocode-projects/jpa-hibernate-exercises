package com.bobocode.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;
import java.util.function.Function;

import com.bobocode.exception.DaoException;

public class EntityManagerUtil {

    private final EntityManagerFactory entityManagerFactory;

    public EntityManagerUtil(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void executeEntityManagerRequest(Consumer<EntityManager> emConsumer) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            emConsumer.accept(entityManager);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new DaoException("Request was not executed! Reason:" + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public <T> T resultListFromEntityManagerRequest(Function<EntityManager, T> emFunction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            T result = emFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new DaoException("Request was not executed! Reason:" + e.getMessage());
        } finally {
            entityManager.close();
        }
    }
}
